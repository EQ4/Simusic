/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import elements.Chord;
import elements.Sequence;
import extractors.chord.ChordExtractor;
import extractors.feature.FeatureExtractor;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.sound.sampled.*;
import markov.MarkovModel;
import sun.applet.Main;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import runners.TestRunner;

/**
 *
 * @author Martin
 */
public class Performer extends Agent {

    String agentFolder;
    String studioGUID;
    String midiPath;
    String featurePath;
    Integer maxMarkovHarmonyDepth;
    ArrayList<Sequence> chords;
    MarkovModel markovHarmonyModel;
    FeatureExtractor fextract;

    boolean isConnectedToStudio;
    
    String managerGUID;

    @Override
    protected void setup() {
        //Get agent folder
        Object[] agentArgs = getArguments();
        agentFolder = (String) agentArgs[0];
        midiPath = agentFolder + "//midi//";
        featurePath = agentFolder + "//features//";
        studioGUID = (String) agentArgs[1];
        managerGUID = (String) agentArgs[2];
        maxMarkovHarmonyDepth = Integer.parseInt((String) agentArgs[3]);
        isConnectedToStudio = false;
        //Save manager GUID

        //Add message behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    String msg = incomingMessage.getContent();
                    if (!isConnectedToStudio) {
                        if (msg.contains("join_request_ack")) {
                            System.out.println("<" + getName() + "> I connected to " + studioGUID);
                            isConnectedToStudio = true;
                        }
                    } else {
                        if (msg.contains("load_yourself")) {
                            load();
                            send(agents.Services.SendMessage(studioGUID, "load_finished"));
                        }
                        if (msg.equals("play_test_count")) {
                            playTestCount();
                        }
                        if (msg.equals("play_test_click")) {
                            playTestClick();
                        }
                        if (msg.equals("get_initial_tempo")) {
                            send(agents.Services.SendMessage(studioGUID, "initial_tempo=" + fextract.getAverageFeatureValue("Initial Tempo")));
                        }
                        if (msg.equals("ping")) {
                            send(agents.Services.SendMessage(studioGUID, "pong"));
                        }
                    }
                }
            }
        });

        //Send connect request to studio
        send(agents.Services.SendMessage(studioGUID, "join_request"));
        
        System.out.println("<" + getName() + "> Hello World! My manager is " + managerGUID);
    }

    private void load() {
        System.out.println("<" + getName() + "> Extracting chords and generating Markov model...");
        
        //Extract chords
        chords = ChordExtractor.extractChordsFromMidiFiles(midiPath);

        //Train Markov model
        markovHarmonyModel = new MarkovModel(maxMarkovHarmonyDepth, new Chord());
        markovHarmonyModel.trainModel(chords);

        System.out.println("<" + getName() + "> Extracting MIDI features...");
        
        //Extract features
        fextract = new FeatureExtractor(midiPath, featurePath, false);
    }

    private void playTestCount() {
        playSound(TestRunner.clickPath + getLocalName() + ".wav");
    }

    private void playTestClick() {
        playSound(TestRunner.clickPath + "hihat.wav");
    }

    private void playSound(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = new FileInputStream(path);
                    AudioStream audioStream = new AudioStream(in);
                    AudioPlayer.player.start(audioStream);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}
