/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import rmi.registry.Registry;

/**
 *
 * @author Martin
 */
public interface HumanPerformerInterface extends Remote {
    
    public void noteOn(int pitch, int velocity) throws RemoteException;
    public void noteOff(int pitch) throws RemoteException;
    
    public void sustainOn() throws RemoteException;
    public void sustainOff() throws RemoteException;
    
    public int connect(Registry.AgentType agentType) throws RemoteException;
    public boolean ping(int id) throws RemoteException;
    public boolean disconnect(int id) throws RemoteException;
}
