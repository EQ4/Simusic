/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import rmi.interfaces.AgentInterface;
import rmi.interfaces.RegistryInterface;

/**
 *
 * @author Martin
 */
public class MonitorDaemon extends UnicastRemoteObject implements AgentInterface {
    
    MonitorFrame frame;
    
    public MonitorDaemon(MonitorFrame frame) throws RemoteException {
        super(frame.monitorServicePort);
        this.frame = frame;
        System.setProperty("java.security.policy", "simusic.policy");
        System.setSecurityManager(new SecurityManager());
    }
    
    //AI methods
    @Override
    public void unicast(String message, int senderID) throws RemoteException {
        
    };
    
    @Override
    public boolean ping() throws RemoteException {
        return true;
    };
    
    @Override
    public boolean disconnect() throws RemoteException {
        return true;
    }
    
    //Monitor methods
    
    @Override
    public String sayHello() throws RemoteException {
        return "Hi from agent!";
    };
    
    @Override
    public void update(UpdateMessage update) throws RemoteException {
        
    };
    
}
