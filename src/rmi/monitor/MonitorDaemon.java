/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
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
    }

    //AI methods
    @Override
    public void unicast(String message, int senderID) throws RemoteException {
        frame.log("Agent " + senderID + " told me '" + message + "'");
    }
    
    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    //Monitor methods
    @Override
    public String sayHello() throws RemoteException {
        frame.log("Registry says hi!");
        return "Hi from agent!";
    }
    
    @Override
    public void update(UpdateMessage update) throws RemoteException {
        frame.log("Monitor view updated");
        frame.processUpdate(update);
    }
    
    @Override
    public boolean connectNeighbour(int optimisticAgentID) {
        frame.log("Agent #" + optimisticAgentID + " is trying to neighbour a monitor.");
        return false;
    }
    
    @Override
    public boolean disconnectNeighbour(int neighbourID) throws RemoteException {
        frame.log("Impossible!");
        return false;
    }
    
    @Override
    public boolean shutdown() throws RemoteException {
        //Simply disconnect from registry
        frame.disconnect();
        frame.log("Registry has shut itself down.");
        return false;
    }
    
    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        //Used mainly in Human and AI performers
        return "I am monitor!";
    }
    
    @Override
    public void startPerformance() throws RemoteException {
        frame.log("Performance started!");
        frame.setPerformingStatus();
    }
}
