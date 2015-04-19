/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import rmi.interfaces.AgentInterface;
import rmi.interfaces.RegistryInterface;
import rmi.misc.AgentType;
import rmi.monitor.AgentDummy;
import rmi.monitor.UpdateMessage;
import run.Main;

/**
 *
 * @author Martin
 */
public abstract class Agent extends UnicastRemoteObject implements Runnable, AgentInterface {

    public String name;
    public int id;
    public String ip;
    public int port;
    public int servicePort;
    public String agentRmiAddress;
    public String registryURL;
    public int masterMonitorID;

    public java.rmi.registry.Registry rmiRegistryLocation;
    private RegistryInterface registryConnection;
    private HashMap<Integer, AgentInterface> neighbourConnections;
    private HashMap<Integer, AgentDummy> neighbourDummies;

    public Agent(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID) throws RemoteException {
        super(servicePort);
        this.name = name;
        this.registryURL = registryURL;
        this.ip = ip;
        this.port = port;
        this.servicePort = servicePort;
        this.masterMonitorID = masterMonitorID;
        this.agentRmiAddress = "rmi://" + ip + ":" + port + "/" + name;

        this.neighbourConnections = new HashMap<>();
        this.neighbourDummies = new HashMap<>();
    }

    public void start() {
        Thread agentDaemon = new Thread(this);
        agentDaemon.setDaemon(true);
        agentDaemon.start();
    }

    @Override
    public void run() {
        try {
            rmiRegistryLocation = java.rmi.registry.LocateRegistry.createRegistry(port);
            Naming.rebind(agentRmiAddress, this);
            connectToRegistry();
            runBehaviour();
        } catch (ExportException e) {
            System.out.println("<" + agentRmiAddress + "> Port is already in use! Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object RMIconnect(String url) {
        try {
            return Naming.lookup(url);
        } catch (Exception e) {
            System.out.println("Agent to registry connection exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void connectToRegistry() throws RemoteException {
        registryConnection = (RegistryInterface) RMIconnect(registryURL);

        //Connect & Update
        UpdateMessage firstUpdate = registryConnection.connect(getAgentType(), name, ip, port, masterMonitorID);
        this.id = firstUpdate.welcomePack.ID;
    }

    public abstract void runBehaviour();

    public abstract AgentType getAgentType();

    @Override
    public void unicast(String message, int senderID) throws RemoteException {
        String logMessage;
        if (senderID == masterMonitorID) {
            logMessage = "My master monitor sent me '" + message + "'";
        } else if (neighbourDummies.containsKey(senderID)) {
            logMessage = neighbourDummies.get(senderID).name + " sent me '" + message + "'";
        } else {
            logMessage = "Non-neighbour (id=" + senderID + " sent me '" + message + "'";
        }
        logInRegistry(logMessage);
    }
    
    private void logInRegistry(String logMessage) throws RemoteException {
        registryConnection.log(logMessage, id);
    }
    
    private void disconnectFromRegistry() throws RemoteException  {
        registryConnection.disconnect(id);
    }

    //Sent by registry or monitor to
    @Override
    public boolean shutdown() throws RemoteException {
        logInRegistry("Shutdown triggered by master monitor #" + masterMonitorID);
        disconnectFromRegistry();
        
        //Remove RMI naming
        try {
            rmiRegistryLocation.unbind(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    //Sent by other agents to become neighbours
    @Override
    public boolean connectNeighbour(int neighbourID) throws RemoteException {
        AgentDummy newNeighbourDummy = registryConnection.getAgentDummyByID(neighbourID);
        AgentInterface newNeighbourConnection = (AgentInterface) RMIconnect(newNeighbourDummy.getRMIAddress());
        if (newNeighbourConnection != null) {
            neighbourDummies.put(neighbourID, newNeighbourDummy);
            neighbourConnections.put(neighbourID, newNeighbourConnection);
            logInRegistry(newNeighbourDummy.name + " connected to me! We are now neighbours.");
            return true;
        }
        return false;
    }

    @Override
    public boolean disconnectNeighbour(int agentID) throws RemoteException {
        neighbourDummies.remove(agentID);
        neighbourConnections.remove(agentID);
        return true;
    }

    @Override
    public String sayHello() throws RemoteException {
        System.out.println("<" + name + "> Someone said hi!");
        return "Hi from " + name + "!";
    }

    @Override
    public void update(UpdateMessage update) throws RemoteException {
        //TODO: Check dummies and links for ones that contain this agent
    }

}
