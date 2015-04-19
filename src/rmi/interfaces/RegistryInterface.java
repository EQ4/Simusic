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
import rmi.misc.AgentType;
import rmi.monitor.AgentDummy;

/**
 *
 * @author Martin
 */
public interface RegistryInterface extends Remote {
    
    public UpdateMessage connect(AgentType agentType, String agentName, String agentIP, int agentPort, Integer masterMonitorID) throws RemoteException;
    public boolean ping(int id) throws RemoteException;
    public AgentDummy getAgentDummyByID(int id) throws RemoteException;
    public void reportNeighbourConnection(int fromAgentID, int toAgentID) throws RemoteException;
    public void log(String message, int loggerID) throws RemoteException;
    public boolean disconnect(int id) throws RemoteException;
    
    //Test
    public String sayHello(String sender) throws RemoteException; 
}
