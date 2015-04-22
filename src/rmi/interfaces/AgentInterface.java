/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.interfaces;

import enums.AuctionType;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import music.elements.Chord;
import music.elements.Playable;
import rmi.messages.AuctionMessage;
import rmi.messages.UpdateMessage;
import rmi.agents.registry.RegistryDaemon;

/**
 *
 * @author Martin
 */
public interface AgentInterface extends Remote  {
    //AI methods
    public boolean connectNeighbour(int agentID) throws RemoteException;
    public boolean ping() throws RemoteException;
    public void update(UpdateMessage update) throws RemoteException;
    public void unicast(String message, int senderID) throws RemoteException;
    public void loadAgent() throws RemoteException;
    public boolean disconnectNeighbour(int agentID) throws RemoteException;
    public boolean shutdown() throws RemoteException;
    public AuctionMessage executeLocalAuction(AuctionType auctionType, String[] args) throws RemoteException;
    public Double getAverageFeature(String featureName) throws RemoteException;
    public abstract String getAgentTypeSpecificInfo() throws RemoteException;
    
    public void performanceStarted(int initialTempo) throws RemoteException;
    public void performanceStopped() throws RemoteException;
    
    public void beat(Chord chord) throws RemoteException;
    public void playSolo() throws RemoteException;
    
    
    //Test
    public String sayHello() throws RemoteException;
}
