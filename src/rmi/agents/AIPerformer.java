/*
 * The MIT License
 *
 * Copyright 2015 Martin Minovski <martin at minovski.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rmi.agents;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import music.elements.Chord;
import music.elements.Sequence;
import music.extractors.content.ContentExtractor;
import music.extractors.feature.FeatureExtractor;
import music.markov.MarkovModel;
import rmi.interfaces.AgentInterface;
import enums.AgentType;
import enums.AuctionType;
import music.elements.Note;
import music.elements.Playable;
import music.extractors.feature.GlobalFeatureContainer;
import music.extractors.instrument.InstrumentExtractor;
import rmi.messages.AuctionMessage;
import rmi.agents.monitor.AgentDummy;
import run.Main;
import static run.Main.NUMBER_OF_SOLO_PHRASES_PER_AGENT;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class AIPerformer extends Agent {

    private File[] midiFiles;
    private File featuresFolder;

    private ArrayList<Sequence> harmonySequences;
    private ArrayList<Sequence> melodyByChordSequences;

    private MarkovModel markovChordModel;
    private MarkovModel[] markovMelodyModels;

    private int markovModelMaxDepth;

    private FeatureExtractor fextract;
    private GlobalFeatureContainer globalFeatures;
    private int lastGlobalChordMarkovInteger;

    private String roleModelInfo;
    private boolean isLoading;
    private int agentInstrument;

    AgentDummy roleModelDummy;
    AgentInterface roleModelConnection;

    /**
     *
     * @param name
     * @param registryURL
     * @param ip
     * @param port
     * @param servicePort
     * @param masterMonitorID
     * @param midiFiles
     * @param markovChordModelMaxDepth
     * @throws RemoteException
     */
    public AIPerformer(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID, File[] midiFiles, int markovChordModelMaxDepth) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);

        //Get paths
        this.midiFiles = midiFiles;
        this.markovModelMaxDepth = markovChordModelMaxDepth;
        this.lastGlobalChordMarkovInteger = 0;
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

    /**
     *
     */
    @Override
    public void loadAgent() {
        //Extract chords
        log("Extracting harmony and melody sequences from MIDI files...", false);
        ContentExtractor cextract = new ContentExtractor(midiFiles, this);
        harmonySequences = cextract.getHarmonySequences();
        melodyByChordSequences = cextract.getMelodyByChordSequences();

        log("Harmonies and melodies extracted!", false);

        //Extract features
        log("Extracting features from MIDI files...", false);
        fextract = new FeatureExtractor(midiFiles, featuresFolder, false, this);

        //Extract instrument
        log("Extracting instrument...", false);
        agentInstrument = InstrumentExtractor.getMostFrequentInstrument(midiFiles);

        //Train Markov harmony and melody models
        log("Training Markov harmony and melody models...", false);

        markovChordModel = new MarkovModel(markovModelMaxDepth, new Chord());
        markovChordModel.trainModel(harmonySequences);
        //TODO: Customize markovChordModelMaxDepth for MELODY
        markovMelodyModels = new MarkovModel[Chord.maxMarkovInteger];
        for (int i = 0; i < Chord.maxMarkovInteger; i++) {
            //Initialize chord-dependent model
            markovMelodyModels[i] = new MarkovModel(markovModelMaxDepth, new Note());
            //Train model
            Sequence singleSequence = melodyByChordSequences.get(i);
            ArrayList<Sequence> singleSequenceList = new ArrayList<>();
            singleSequenceList.add(singleSequence);
            markovMelodyModels[i].trainModel(singleSequenceList);
        }

        log("Trained Markov harmony and melody models with max depth " + markovModelMaxDepth, false);

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
    /**
     *
     * @param neighbourID
     * @return
     * @throws RemoteException
     */
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

    /**
     *
     * @param auctionType
     * @param args
     * @return
     * @throws RemoteException
     */
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
                log("I don't know what the next chord should be. Flushing my live queue...", false);
                resultMessage.chordBase = "A";
                resultMessage.chordMode = "min";
                resultMessage.chordProbability = 0.0000001;
                resultMessage.isDefaultChord = true;
                markovChordModel.liveFlush();
            }

            //Log auction result
            String ballotContent = "";
            for (AuctionMessage message : ballotBox) {
                ballotContent += "\n\n\tChord base: " + message.chordBase;
                ballotContent += "\n\tChord mode: " + message.chordMode;
                ballotContent += "\n\tChord utility: " + message.chordProbability;
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

    /**
     *
     * @param currentTempo
     * @throws RemoteException
     */
    @Override
    public void performanceStarted(int currentTempo) throws RemoteException {
        log("Performance started with tempo " + currentTempo + ", beat period " + Main.getBeatPeriod(currentTempo), false);
    }

    /**
     *
     * @throws RemoteException
     */
    @Override
    public void performanceStopped() throws RemoteException {
        log("Performance stopped!", false);
    }

    /**
     *
     * @param chord
     * @throws RemoteException
     */
    @Override
    public void beat(Chord chord) throws RemoteException {
        new Thread() {
            @Override
            public void run() {
                updateGlobalFeatures();

                lastGlobalChordMarkovInteger = chord.getMarkovInteger();
                markovChordModel.livePush(chord);
                Main.player.playHarmony(chord, agentInstrument, fextract, globalFeatures);

                //Uncomment to test latency
                //logPrecise("I played " + chord.toString());
            }
        }.start();
    }

    /**
     *
     * @throws RemoteException
     */
    @Override
    public void playSolo() throws RemoteException {
        updateGlobalFeatures();

        log("I started soloing!", true);

        for (int i = 0; i < NUMBER_OF_SOLO_PHRASES_PER_AGENT; i++) {
            MarkovModel currentMelodyModel = markovMelodyModels[lastGlobalChordMarkovInteger];
            Sequence soloSequence = new Sequence();
            Double lastNoteProbability = Double.MIN_NORMAL;
            
            //3, 4, 6 or 8
            int numberOfNotes = (3 + Main.rand.nextInt(2)) * (1 + Main.rand.nextInt(2));
            
            
            for (int j = 0; j < numberOfNotes; j++) {
                if (Double.isNaN(lastNoteProbability)) {
                    break;
                }
                Note nextNote = (Note) currentMelodyModel.getNextPlayable();
                soloSequence.appendPlayable(nextNote);
                lastNoteProbability = nextNote.getProbability();
                currentMelodyModel.livePush(nextNote);
            }
            currentMelodyModel.liveFlush();
            Main.player.playSoloPhrase(soloSequence, agentInstrument, fextract, globalFeatures);
        }

        log("I finished soloing!", true);
    }

    private void updateGlobalFeatures() {
        try {
            globalFeatures = registryConnection.getGlobalFeatures();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public AgentType getAgentType() {
        return AgentType.AIPerformer;
    }

    /**
     *
     * @return @throws RemoteException
     */
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

    /**
     *
     * @param featureName
     * @return
     * @throws RemoteException
     */
    @Override
    public Double getAverageFeature(String featureName) throws RemoteException {
        if (fextract.hasFeature(featureName)) {
            return fextract.getAverageFeatureValue(featureName);
        }
        //Else - unknown feature name
        return null;
    }
}
