/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.dummies;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class AiPerformerDummy extends AgentDummy {
    
    ArrayList<String> neighbours;
    
    int numberOfMidiFiles;
    int maxMarkovChain;
    int numberOfMidiFeatures;
    
    public AiPerformerDummy(String name, int id, InetAddress address, int numberOfMidiFiles, int maxMarkovChain, int numberOfMidiFeatures) {
        super(name, id, address);
        neighbours = new ArrayList<>();
        this.numberOfMidiFiles = numberOfMidiFiles;
        this.maxMarkovChain = maxMarkovChain;
        this.numberOfMidiFeatures = numberOfMidiFeatures;
    }
    
    @Override
    public String getIconFilename() {
        return "aiperformer";
    }
}
