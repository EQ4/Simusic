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

/**
 *
 * @author Martin
 */
public class Performer extends Agent {

    String agentFolder;
    String studioAddress;
    String midiPath;
    String featurePath;
    Integer maxMarkovHarmonyDepth;
    ArrayList<Sequence> chords;
    MarkovModel markovHarmonyModel;
    FeatureExtractor fextract;

    @Override
    protected void setup() {
        //Get agent folder
        Object[] agentArgs = getArguments();
        agentFolder = (String) agentArgs[0];
        midiPath = agentFolder + "//midi//";
        featurePath = agentFolder + "//features//";
        studioAddress = (String) agentArgs[1];
        maxMarkovHarmonyDepth = Integer.parseInt((String) agentArgs[2]);


        //Add message behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    String msg = incomingMessage.getContent();
                    if (msg.contains("load_yourself")) {
                        load();
                        send(agents.Services.SendMessage(studioAddress, "load_finished"));
                    }
                    if (msg.equals("play_test_count")) {
                        playTestCount();
                    }
                    if (msg.equals("play_test_click")) {
                        playTestClick();
                    }
                    if (msg.equals("get_initial_tempo")) {
                        send(agents.Services.SendMessage(studioAddress, "initial_tempo=" + fextract.getAverageFeatureValue("Initial Tempo")));
                    }
                }
            }
        });

        //Send idle message
        send(agents.Services.SendMessage(studioAddress, "I am initialized!"));
    }

    private void load() {
        //Extract chords
        chords = ChordExtractor.extractChordsFromMidiFiles(midiPath);

        //Train Markov model
        markovHarmonyModel = new MarkovModel(maxMarkovHarmonyDepth, new Chord());
        markovHarmonyModel.trainModel(chords);

        //Extract features
        fextract = new FeatureExtractor(midiPath, featurePath, false);
    }

    private void playTestCount() {
        playSound("D:\\Desktop\\Dissertation\\_runtime\\click_sounds\\" + getLocalName() + ".wav");
    }
    
    private void playTestClick() {
        playSound("D:\\Desktop\\Dissertation\\_runtime\\click_sounds\\hihat.wav");
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
