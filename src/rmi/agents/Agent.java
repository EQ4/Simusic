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
import rmi.interfaces.AgentInterface;
import rmi.interfaces.RegistryInterface;
import rmi.misc.AgentType;
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

    private java.rmi.registry.Registry rmiRegistryLocation;
    private RegistryInterface registryConnection;

    public Agent(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID) throws RemoteException {
        super(servicePort);
        this.name = name;
        this.registryURL = registryURL;
        this.ip = ip;
        this.port = port;
        this.servicePort = servicePort;
        this.masterMonitorID = masterMonitorID;
        this.agentRmiAddress = "rmi://" + ip + ":" + port + "/" + name;
    }

    public void startAgentDaemon() {
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

    private void connectToRegistry() throws RemoteException {
        System.setSecurityManager(new SecurityManager());
        try {
            registryConnection = (RegistryInterface) Naming.lookup(registryURL);
        } catch (Exception e) {
            System.out.println("Agent to registry connection exception: " + e.getMessage());
            e.printStackTrace();
        }

        //Connect & Update
        UpdateMessage firstUpdate = registryConnection.connect(getAgentType(), name, ip, port, masterMonitorID);
        this.id = firstUpdate.welcomePack.ID;
    }

    public abstract void runBehaviour();

    public abstract AgentType getAgentType();

    @Override
    public void unicast(String message, int senderID) throws RemoteException {

    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    @Override
    public boolean disconnect() throws RemoteException {
        return true;
    }

    @Override
    public String sayHello() throws RemoteException {
        System.out.println("<" + name + "> Someone said hi!");
        return "Hi from " + name + "!";
    }

    @Override
    public void update(UpdateMessage update) throws RemoteException {

    }

}
