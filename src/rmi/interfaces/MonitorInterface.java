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
import rmi.elements.UpdateMessage;

/**
 *
 * @author Martin
 */
public interface MonitorInterface extends Remote {
    public UpdateMessage getFullUpdate() throws RemoteException;
    public UpdateMessage getPartialUpdate() throws RemoteException;
    public String sayHello() throws RemoteException;
    
    public int connect(Registry.AgentType agentType) throws RemoteException;
    public boolean ping(int id) throws RemoteException;
    public boolean disconnect(int id) throws RemoteException;
}
