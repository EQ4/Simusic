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

    }

    ;
    
    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    ;
    
    //Monitor methods
    
    @Override
    public String sayHello() throws RemoteException {
        frame.log("Registry says hi!");
        return "Hi from agent!";
    }

    ;
    
    @Override
    public void update(UpdateMessage update) throws RemoteException {
        //Process the update:
        frame.processUpdate(update);
    }

    ;
    
    @Override
    public boolean connectNeighbour(int neighbourID) {
        //No agent becomes neighbour with monitor
        return false;
    }

    @Override
    public boolean disconnectNeighbour(int neighbourID) throws RemoteException {
        //No agent becomes neighbour with monitor
        return false;
    }

    @Override
    public boolean shutdown() throws RemoteException {
        //Simply disconnect from registry
        frame.disconnect();
        frame.log("Registry has shut itself down.");
        return false;
    }
}
