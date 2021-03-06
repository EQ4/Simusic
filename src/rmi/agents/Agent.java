/*
 * The MIT License
 *
 * Copyright 2015 Martin Minovski <martin at minovski.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
 * Agent abstract class. All basic agent functionality is in this class
 * @author Martin Minovski <martin at minovski.net>
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
    /**
     * Implements the interface method, but inheriting classes must override it
     * @throws RemoteException
     */
    @Override
    public abstract void loadAgent() throws RemoteException;

    public abstract AgentType getAgentType();
    @Override
    public abstract String getAgentTypeSpecificInfo() throws RemoteException;
    @Override
    public abstract void performanceStarted(int initialTempo) throws RemoteException;
    @Override
    public abstract void performanceStopped() throws RemoteException;

    /**
     * Abstract constructor, used by specific agents
     * @param name
     * @param registryURL
     * @param ip
     * @param port
     * @param servicePort
     * @param masterMonitorID
     * @throws RemoteException
     */
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

    /**
     * This thread starts the agent daemon (its RMI registry, etc)
     */
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
        } catch (ExportException e) {
            log("Port is already in use! Please try again later.", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to a registry
     * @param url
     * @return
     */
    public Object RMIconnect(String url) {
        try {
            return Naming.lookup(url);
        } catch (Exception e) {
            log("Agent to registry connection exception: " + e.getMessage(), false);
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

    /**
     * Logs in console with precise time and agent name
     * @param message
     * @param precise Adds milliseconds if true
     */
    public void log(String message, boolean precise) {
        System.out.println(Main.getCurrentTimestamp(precise) + "<" + name + ", " + getAgentType().toString() + " #" + agentID + "> " + message);
    }

    /**
     * Logs message in the registry.
     * @param logMessage
     * @throws RemoteException
     */
    public void logInRegistry(String logMessage) throws RemoteException {
        registryConnection.log(logMessage, agentID);
    }

    /**
     * Simple unicast messaging service
     * @param message
     * @param senderID
     * @throws RemoteException
     */
    @Override
    public void unicast(String message, int senderID) throws RemoteException {
        String logMessage;
        if (senderID == masterMonitorID) {
            logMessage = "My master monitor sent me '" + message + "'";
        } else if (neighbourDummies.containsKey(senderID)) {
            logMessage = neighbourDummies.get(senderID).name + " sent me '" + message + "'";
        } else if (message.contains("TO-MONITORS-ONLY")) {
            //Ignore message
            return;
        } else {
            logMessage = "Non-neighbour (id=" + senderID + " sent me '" + message + "'";
        }
        logInRegistry(logMessage);
    }

    /**
     * Sent by registry or monitor to indicate shutdown
     * @return @throws RemoteException
     */
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

    /**
     * Ping service, used to calculate latency
     * @return @throws RemoteException
     */
    @Override
    public boolean ping() throws RemoteException {
        log("Someone pinged me!", false);
        return true;
    }

    //Sent by other AI agents to become neighbours
    /**
     *
     * @param neighbourID
     * @return
     * @throws RemoteException
     */
    @Override
    public abstract boolean connectNeighbour(int neighbourID) throws RemoteException;

    /**
     * Agent is no longer role model of the specified agent
     * @param agentID
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean disconnectNeighbour(int agentID) throws RemoteException {
        //To do in future
        neighbourDummies.remove(agentID);
        neighbourConnections.remove(agentID);
        return true;
    }

    /**
     * Say hi!
     * @return @throws RemoteException
     */
    @Override
    public String sayHello() throws RemoteException {
        log("Someone said hi!", false);
        return "Hi from " + name + "!";
    }

    /**
     * Check dummies and links for ones that contain this agent
     * @param update
     * @throws RemoteException
     */
    @Override
    public void update(UpdateMessage update) throws RemoteException {
        //TODO: Check dummies and links for ones that contain this agent
    }
}
