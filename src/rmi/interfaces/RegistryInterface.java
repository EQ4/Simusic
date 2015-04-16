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
import rmi.registry.Registry;
import rmi.misc.AgentType;

/**
 *
 * @author Martin
 */
public interface RegistryInterface extends Remote {
    //AI methods
    public void broadcast(String message) throws RemoteException;
    public void unicast(String message, String recipient) throws RemoteException;
    public void multicast(String message, ArrayList<String> recipients) throws RemoteException;
    public int connect(AgentType agentType) throws RemoteException;
    public boolean ping(int id) throws RemoteException;
    public boolean disconnect(int id) throws RemoteException;
    
    //Monitor methods
    
    public String sayHello() throws RemoteException;
    
    
    //Human methods
    
    
    public void noteOn(int pitch, int velocity) throws RemoteException;
    public void noteOff(int pitch) throws RemoteException;
    
    public void sustainOn() throws RemoteException;
    public void sustainOff() throws RemoteException;
}
