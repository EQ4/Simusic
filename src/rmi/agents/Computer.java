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
import rmi.misc.AgentType;
import rmi.monitor.AgentDummy;
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
    private int markovModelMaxDepth;
    private String roleModelDistance;

    AgentDummy roleModelDummy;
    AgentInterface roleModelConnection;

    public Computer(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID, File[] midiFiles, int markovModelMaxDepth) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);

        //Get paths
        this.midiFiles = midiFiles;
        this.markovModelMaxDepth = markovModelMaxDepth;
        if (midiFiles.length != 0) {
            try {
                featuresFolder = new File(midiFiles[0].getParentFile().getCanonicalPath() + "/simusic/features");
            } catch (Exception e) {
                e.printStackTrace();
            }
            featuresFolder.mkdirs();
        }
    }

    @Override
    public void loadAgent() {
        //Extract chords
        chords = ChordExtractor.extractChordsFromMidiFiles(midiFiles, name);

        //Extract features
        fextract = new FeatureExtractor(midiFiles, featuresFolder, false, name);

        //Train Markov model
        markovChordModel = new MarkovModel(markovModelMaxDepth, new Chord());
        markovChordModel.trainModel(chords);
        log("Trained Markov model with max depth " + markovModelMaxDepth);

        try {
            //Get neighbour ID from registry
            roleModelDummy = registryConnection.getRoleModel(id, fextract.getAverageFeatureValues());

            //Connect to neighbour
            if (roleModelDummy != null) {
                roleModelConnection = (AgentInterface) Naming.lookup(roleModelDummy.getRMIAddress());
                roleModelConnection.connectNeighbour(id);
                log("My role model is " + roleModelDummy.name + " with distance of " + roleModelDummy.roleModelMessage);
                roleModelDistance = roleModelDummy.roleModelMessage;
            }

            //Finished loading - report to registry
            log("Loading finished!");
            registryConnection.agentLoaded(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPerformance() {
        System.out.println("Computer is performing");
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.AIPerformer;
    }

    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        String result = "I am AI agent!\n";
        if ((midiFiles != null) && (featuresFolder != null)) {
            result += "Files: " + Arrays.toString(midiFiles) + "\n"
                    + "Folder: " + featuresFolder.getPath() + "\n"
                    + ((roleModelDummy != null) ? "Role Model: " + roleModelDummy.name + ", distance: " + roleModelDistance + "\n" : "")
                    + "\n";
        }
        return result;
    }

}
