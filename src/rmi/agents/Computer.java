/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import music.elements.Chord;
import music.elements.Sequence;
import music.extractors.chord.ChordExtractor;
import music.extractors.feature.FeatureExtractor;
import music.markov.MarkovModel;
import rmi.interfaces.AgentInterface;
import enums.AgentType;
import enums.AuctionType;
import music.elements.Playable;
import music.extractors.instrument.InstrumentExtractor;
import rmi.messages.AuctionMessage;
import rmi.agents.monitor.AgentDummy;
import run.Main;

/**
 *
 * @author Martin
 */
public class Computer extends Agent {

    private File[] midiFiles;
    private File featuresFolder;

    private ArrayList<Sequence> chords;
    private MarkovModel markovChordModel;
    private FeatureExtractor fextract;
    private int markovChordModelMaxDepth;
    private String roleModelInfo;
    private boolean isLoading;
    private int currentTempo;
    private int agentInstrument;

    AgentDummy roleModelDummy;
    AgentInterface roleModelConnection;

    public Computer(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID, File[] midiFiles, int markovChordModelMaxDepth) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);

        //Get paths
        this.midiFiles = midiFiles;
        this.markovChordModelMaxDepth = markovChordModelMaxDepth;
        if (midiFiles.length != 0) {
            try {
                featuresFolder = new File(midiFiles[0].getParentFile().getCanonicalPath() + "/simusic/features");
            } catch (Exception e) {
                e.printStackTrace();
            }
            featuresFolder.mkdirs();
        }

        this.isLoading = true;
    }

    @Override
    public void loadAgent() {
        //Extract chords
        log("Extracting chord sequences from MIDI files...", false);
        chords = ChordExtractor.extractChordsFromMidiFiles(midiFiles, this);
        log("Chord sequences extracted!", false);

        //Extract features
        log("Extracting features from MIDI files...", false);
        fextract = new FeatureExtractor(midiFiles, featuresFolder, false, this);

        //Extract instrument
        log("Extracting instrument...", false);
        agentInstrument = InstrumentExtractor.getMostFrequentInstrument(midiFiles);

        //Train Markov chord model
        log("Training Markov model...", false);
        markovChordModel = new MarkovModel(markovChordModelMaxDepth, new Chord());
        markovChordModel.trainModel(chords);
        log("Trained Markov chord model with max depth " + markovChordModelMaxDepth, false);

        try {
            //Get neighbour ID from registry
            roleModelDummy = registryConnection.getRoleModel(agentID, fextract.getAverageFeatureValues());

            //Connect to neighbour
            if (roleModelDummy != null) {
                roleModelConnection = (AgentInterface) Naming.lookup(roleModelDummy.getRMIAddress());
                roleModelConnection.connectNeighbour(agentID);
                log("My role model is " + roleModelDummy.name + " with distance of " + roleModelDummy.roleModelMessage, false);
                roleModelInfo = roleModelDummy.roleModelMessage;
            }

            //Finished loading - report to registry
            log("Loading finished!", false);
            this.isLoading = false;
            registryConnection.agentLoaded(agentID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Sent by other AI agents to become neighbours
    @Override
    public boolean connectNeighbour(int neighbourID) throws RemoteException {
        AgentDummy newNeighbourDummy = registryConnection.getAgentDummyByID(neighbourID);
        AgentInterface newNeighbourConnection = (AgentInterface) RMIconnect(newNeighbourDummy.getRMIAddress());
        if (newNeighbourConnection != null) {
            neighbourDummies.put(neighbourID, newNeighbourDummy);
            neighbourConnections.put(neighbourID, newNeighbourConnection);
            registryConnection.reportNeighbourConnection(neighbourID, agentID);
            log(newNeighbourDummy.name + " connected to me! I am his role model.", false);
            return true;
        }
        return false;
    }

    @Override
    public synchronized AuctionMessage executeLocalAuction(AuctionType auctionType, String[] args) throws RemoteException {
        AuctionMessage resultMessage = new AuctionMessage(auctionType);

        // Chord auction
        if (auctionType == AuctionType.ChordAuction) {
            Chord nextChord = (Chord) markovChordModel.getNextPlayable();
            resultMessage.chordBase = nextChord.getBaseLetter();
            resultMessage.chordMode = nextChord.getMode();
            resultMessage.chordProbability = nextChord.getProbability();
            resultMessage.chordOriginID = agentID;
            //Recursive case

            ArrayList<AuctionMessage> ballotBox = new ArrayList<>();

            //Add neighbour chords to box (distributed recursive call)
            for (AgentInterface neighbourConnection : neighbourConnections.values()) {
                AuctionMessage response = neighbourConnection.executeLocalAuction(auctionType, args);
                ballotBox.add(response);
            }

            //Pick best chord
            for (AuctionMessage message : ballotBox) {
                if (message.chordProbability > resultMessage.chordProbability) {
                    resultMessage.chordBase = message.chordBase;
                    resultMessage.chordMode = message.chordMode;
                    resultMessage.chordProbability = message.chordProbability;
                    resultMessage.chordOriginID = message.chordOriginID;
                }
            }

            //Influence the winning chord probability with own probability for same chord
            ArrayList<Playable> probabilities = markovChordModel.getCondensedProcessedProbabilities();
            Chord winningChord = new Chord(resultMessage.chordBase, resultMessage.chordMode);
            int winningChordMarkovInteger = winningChord.getMarkovInteger();
            double myProbabilityOfWinningChord = probabilities.get(winningChordMarkovInteger).getProbability();
            //Get the mean of winning and own probability
            double newProbability = ((resultMessage.chordProbability + myProbabilityOfWinningChord) / 2);

            if (Double.isNaN(newProbability)) {
                resultMessage.chordProbability = newProbability;
            }

            if (Double.isNaN(resultMessage.chordProbability) || resultMessage.chordProbability == 0) {
                //log("No one knows a next chord. Mutating...", false);
                String[] newChord = new Chord().getNewPlayableFromMarkovNumeric(Main.rand.nextInt(24)).toString().split("-");
                resultMessage.chordBase = newChord[0];
                resultMessage.chordMode = newChord[1];
                resultMessage.chordProbability = 0.0000001;
                resultMessage.isMutatedChord = true;
            }

            //Log auction result
            String ballotContent = "";
            for (AuctionMessage message : ballotBox) {
                ballotContent += "\n\n\tChord base: " + message.chordBase;
                ballotContent += "\n\tChord mode: " + message.chordMode;
                ballotContent += "\n\tChord probability: " + message.chordProbability;
                ballotContent += "\n\tOrigin ID: " + message.chordOriginID;
            }
            //log("My local CHORD Auction was won by " + resultMessage.chordOriginID + " and the result is " + resultMessage.chordBase + "-" + resultMessage.chordMode + ", probability " + resultMessage.chordProbability + "Ballot content: " + ballotContent, false);
        }

        // Feature auction
        if (auctionType == AuctionType.FeatureAuction) {
            resultMessage.featureName = args[0];
            if (neighbourConnections.isEmpty()) {
                //Base case
                resultMessage.feature = fextract.getAverageFeatureValue(resultMessage.featureName);
            } else {
                //Recursive case

                //Get average neighbour feature
                Double featureSum = (double) 0;
                for (AgentInterface neighbourConnection : neighbourConnections.values()) {
                    featureSum += neighbourConnection.executeLocalAuction(auctionType, args).feature;
                }
                resultMessage.feature = featureSum / neighbourConnections.size();

                //Influence feature from local auction with own value of feature
                //Mean again
                resultMessage.feature = (resultMessage.feature + fextract.getAverageFeatureValue(resultMessage.featureName)) / 2;
            }
            log("My local FEATURE Auction result is: " + resultMessage.featureName + " = " + resultMessage.feature, false);
        }

        return resultMessage;
    }

    @Override
    public void performanceStarted(int currentTempo) throws RemoteException {
        this.currentTempo = currentTempo;
        log("Performance started with tempo " + currentTempo + ", beat period " + Main.getBeatPeriod(currentTempo), false);
    }

    @Override
    public void performanceStopped() throws RemoteException {
        log("Performance stopped!", false);
    }

    @Override
    public void beat(Chord chord) throws RemoteException {
        new Thread() {
            @Override
            public void run() {
                // TODO: Play chord using beatPeriod (or not)
                int currentBeatPeriod = Main.getBeatPeriod(currentTempo);

                markovChordModel.livePush(chord);
                Main.player.playArpeggio(chord, agentInstrument, currentBeatPeriod * 4, 4, 70);

                //Uncomment to test latency
                //logPrecise("I played " + chord.toString());
            }
        }.start();
    }

    @Override
    public void playSolo() throws RemoteException {
        // TODO: Play solo using beatPeriod 
        // ON THE SAME THREAD!

        log("I started soloing!", true);

        //TODO: Solo.
        Main.wait(8000);

        log("I finished soloing!", true);
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.AIPerformer;
    }

    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        String result = "I am AI agent!\n";
        if ((midiFiles != null) && (featuresFolder != null)) {
            result += "Files: " + Arrays.toString(midiFiles) + "\n\n"
                    + "Folder: " + featuresFolder.getPath() + "\n\n"
                    + ((roleModelDummy != null) ? "Role Model: " + roleModelDummy.name + ", info: \n" + roleModelInfo + "\n\n" : "")
                    + ((!isLoading) ? markovChordModel.getCondensedProcessedSortedProbabilityString() : "Agent still loading markov model\n")
                    + ((!isLoading) ? fextract.getAverageFeatures() + "\n" : "Agent still loading features")
                    + "\n";
        }
        return result;
    }

    @Override
    public Double getAverageFeature(String featureName) throws RemoteException {
        if (fextract.hasFeature(featureName)) {
            return fextract.getAverageFeatureValue(featureName);
        }
        //Else - unknown feature name
        return null;
    }
}
