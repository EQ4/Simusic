/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import rmi.monitor.UpdateMessage;
import rmi.registry.RegistryDaemon;

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
    public boolean disconnectNeighbour(int agentID) throws RemoteException;
    public boolean shutdown() throws RemoteException;
    
    
    //Test
    public String sayHello() throws RemoteException;
}
