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
import music.player.Arpeggiator;
import org.jfugue.player.Player;
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
        chords = ChordExtractor.extractChordsFromMidiFiles(midiFiles, this);

        //Extract features
        fextract = new FeatureExtractor(midiFiles, featuresFolder, false, this);

        //Train Markov chord model
        markovChordModel = new MarkovModel(markovChordModelMaxDepth, new Chord());
        markovChordModel.trainModel(chords);
        log("Trained Markov chord model with max depth " + markovChordModelMaxDepth);

        try {
            //Get neighbour ID from registry
            roleModelDummy = registryConnection.getRoleModel(agentID, fextract.getAverageFeatureValues());

            //Connect to neighbour
            if (roleModelDummy != null) {
                roleModelConnection = (AgentInterface) Naming.lookup(roleModelDummy.getRMIAddress());
                roleModelConnection.connectNeighbour(agentID);
                log("My role model is " + roleModelDummy.name + " with distance of " + roleModelDummy.roleModelMessage);
                roleModelInfo = roleModelDummy.roleModelMessage;
            }

            //Finished loading - report to registry
            log("Loading finished!");
            this.isLoading = false;
            registryConnection.agentLoaded(agentID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AuctionMessage executeLocalAuction(AuctionType auctionType, String[] args) throws RemoteException {
        AuctionMessage resultMessage = new AuctionMessage(auctionType);

        // Chord auction
        if (auctionType == AuctionType.ChordAuction) {
            if (neighbourConnections.isEmpty()) {
                //Base case
                resultMessage.chord = (Chord) markovChordModel.getNextPlayable();
                resultMessage.chordOriginID = agentID;
            } else {
                //Recursive case

                ArrayList<AuctionMessage> ballotBox = new ArrayList<>();

                //Add neighbour chords to box (distributed recursive call)
                for (AgentInterface neighbourConnection : neighbourConnections.values()) {
                    AuctionMessage ownLocalAuctionResultMessage = neighbourConnection.executeLocalAuction(auctionType, args);
                    ballotBox.add(ownLocalAuctionResultMessage);
                }

                //Pick best chord
                resultMessage.chord = new Chord();
                resultMessage.chord.setProbability(-1);
                for (AuctionMessage message : ballotBox) {
                    if (message.chord.getProbability() > resultMessage.chord.getProbability()) {
                        resultMessage.chord = message.chord;
                        resultMessage.chordOriginID = message.chordOriginID;
                    }
                }
                //Influence winning chord probability with own probability for same chord
                Playable myChordSameAsWinning = markovChordModel.getTopCondensedProcessedPlayableWhichEquals(resultMessage.chord);
                //Get the mean of winning and own probability
                resultMessage.chord.setProbability((resultMessage.chord.getProbability() + myChordSameAsWinning.getProbability()) / 2);
            }
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
        }

        return resultMessage;
    }

    @Override
    public void performanceStarted(int currentTempo) throws RemoteException {
        this.currentTempo = currentTempo;
        log("Performance started with tempo " + currentTempo + ", beat period " + Main.getBeatPeriod(currentTempo));
    }

    @Override
    public void performanceStopped() throws RemoteException {
        log("Performance stopped!");
    }

    @Override
    public void beat(Chord chord) throws RemoteException {
        int currentBeatPeriod = Main.getBeatPeriod(currentTempo);
        // TODO: Play chord using beatPeriod
        new Thread() {
            @Override
            public void run() {
                // Play chord / arpeggio 
                //Main.player.playArpeggio(chord, beatPeriod);
                logPrecise("I played a chord!");
            }
        }.start();
    }

    @Override
    public void playSolo() throws RemoteException {
        // TODO: Play solo using beatPeriod 
        // ON THE SAME THREAD!

        logPrecise("I started soloing!");

        //TODO: Solo.
        Main.wait(8000);

        logPrecise("I finished soloing!");
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
