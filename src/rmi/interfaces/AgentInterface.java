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
public interface AgentInterface extends Remote {
    //AI methods
    public void unicast(String message, int senderID) throws RemoteException;
    public boolean ping() throws RemoteException;
    public boolean disconnect() throws RemoteException; // also human
    
    //Monitor methods
    
    public String sayHello() throws RemoteException;
    public void update(UpdateMessage update) throws RemoteException;
    
    
    //Human methods
}
