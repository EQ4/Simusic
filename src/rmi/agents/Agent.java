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
import enums.AgentType;
import rmi.agents.monitor.AgentDummy;
import rmi.messages.UpdateMessage;
import run.Main;

/**
 *
 * @author Martin
 */
public abstract class Agent extends UnicastRemoteObject implements Runnable, AgentInterface {

    public String name;
    public int agentID;
    public String ip;
    public int port;
    public int servicePort;
    public String agentRmiAddress;
    public String registryURL;
    public int masterMonitorID;

    private java.rmi.registry.Registry rmiRegistryLocation;
    public HashMap<Integer, AgentInterface> neighbourConnections;
    public HashMap<Integer, AgentDummy> neighbourDummies;
    public RegistryInterface registryConnection;

    //Abstract methods
    public abstract void loadAgent();

    public abstract AgentType getAgentType();

    @Override
    public abstract String getAgentTypeSpecificInfo() throws RemoteException;

    @Override
    public abstract void performanceStarted(int initialTempo) throws RemoteException;

    @Override
    public abstract void performanceStopped() throws RemoteException;

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
            loadAgent();
        } catch (ExportException e) {
            log("Port is already in use! Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object RMIconnect(String url) {
        try {
            return Naming.lookup(url);
        } catch (Exception e) {
            log("Agent to registry connection exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void connectToRegistry() throws RemoteException {
        registryConnection = (RegistryInterface) RMIconnect(registryURL);

        //Connect & Update
        UpdateMessage firstUpdate = registryConnection.connect(getAgentType(), name, ip, port, masterMonitorID);
        this.agentID = firstUpdate.welcomePack.agentID;
    }

    private void disconnectFromRegistry() throws RemoteException {
        registryConnection.disconnect(agentID);
    }

    public void log(String message) {
        System.out.println(Main.getCurrentTimestamp() + "<" + name + ", " + getAgentType().toString() + " #" + agentID + "> " + message);
    }

    public void logPrecise(String message) {
        System.out.println("[" + System.currentTimeMillis() + "] <" + name + ", " + getAgentType().toString() + " #" + agentID + "> " + message);
    }

    public void logInRegistry(String logMessage) throws RemoteException {
        registryConnection.log(logMessage, agentID);
    }

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
        log("Someone pinged me!");
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
            registryConnection.reportNeighbourConnection(neighbourID, agentID);
            logInRegistry(newNeighbourDummy.name + " connected to me! I am his role model.");
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
        log("Someone said hi!");
        return "Hi from " + name + "!";
    }

    @Override
    public void update(UpdateMessage update) throws RemoteException {
        //TODO: Check dummies and links for ones that contain this agent
    }
}
