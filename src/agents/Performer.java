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
import java.util.ArrayList;
import markov.MarkovModel;
/**
 *
 * @author Martin
 */
public class Performer extends Agent {
    String agentFolder;
    String studioAddress;
    
    static ArrayList<Sequence> chords;
    static MarkovModel markovHarmonyModel;
    
    @Override
    protected void setup() {
        //Get agent folder
        Object[] agentArgs = getArguments();
        agentFolder = (String)agentArgs[0];
        String midiPath = agentFolder + "//midi//";
        String featurePath = agentFolder + "//features//";
        studioAddress = (String)agentArgs[1];
        Integer maxMarkovHarmonyDepth = Integer.parseInt((String)agentArgs[2]);
        
        
        
        
        
        
        //Initialize agent vars
        //Extract chords
        chords = ChordExtractor.extractChordsFromMidiFiles(midiPath);
        
        //Train Markov model
        markovHarmonyModel = new MarkovModel(maxMarkovHarmonyDepth, new Chord());
        markovHarmonyModel.trainModel(chords);
        
        //Extract features
        FeatureExtractor fextract = new FeatureExtractor(midiPath, featurePath);
        
        
        
        
        
        
        
        
        
        
        //Add message behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    System.out.println(getLocalName() + ": I received '" + incomingMessage.getContent() + "' from " + incomingMessage.getSender().getLocalName());
                }
            }
        });

        //Send ready message
        send(agents.Services.SendMessage(studioAddress, "I am ready to go!"));
    }
}
