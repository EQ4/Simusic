/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents;

import java.rmi.RemoteException;
import enums.AgentType;
import enums.AuctionType;
import music.elements.Chord;
import music.elements.Playable;
import rmi.messages.AuctionMessage;
import run.Main;

/**
 *
 * @author Martin
 */
public class Human extends Agent {

    public Human(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);
    }

    @Override
    public void loadAgent() {
        //TODO: Select MIDI USB port, etc.
        try {
            registryConnection.agentLoaded(agentID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void performanceStarted(int initialTempo) {
        log("Performance started at tempo " + initialTempo, false);
    }

    @Override
    public void performanceStopped() {
        log("Performance stopped", false);
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.HumanPerformer;
    }

    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        return "I am human!";
    }

    @Override
    public void playSolo() {
        // Human plays all the time or...?
        // Idea: Human could steal the limelight
        // ...while agents listen to the human performance and learn (patterns, tempo, dynamics, etc).
    }

    @Override
    public void beat(Chord chord) throws RemoteException {
        // Humans can play chords on a guitar without having to call their void methods Now.
    }

    @Override
    public Double getAverageFeature(String featureName) throws RemoteException {
        //Not Applicable
        return (double) -1;
    }

    @Override
    public AuctionMessage executeLocalAuction(AuctionType auctionType, String[] args) throws RemoteException {
        //Not Applicable
        return null;
    }
    
    @Override
    public boolean connectNeighbour(int neighbourID) throws RemoteException {
        //Not applicable
        return false;
    }
}
