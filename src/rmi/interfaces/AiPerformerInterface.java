/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import rmi.registry.Registry;

/**
 *
 * @author Martin
 */
public interface AiPerformerInterface extends Remote {
    
    public void broadcast(String message) throws RemoteException;
    public void unicast(String message, String recipient) throws RemoteException;
    public void multicast(String message, ArrayList<String> recipients) throws RemoteException;
    
    public int connect(Registry.AgentType agentType) throws RemoteException;
    public boolean ping(int id) throws RemoteException;
    public boolean disconnect(int id) throws RemoteException;
}
