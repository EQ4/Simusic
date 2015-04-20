/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents;

import java.rmi.RemoteException;
import rmi.misc.AgentType;
import run.Main;

/**
 *
 * @author Martin
 */
public class Human extends Agent {

    public Human(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);
    }

    @Override
    public void loadAgent() {
        //Select MIDI USB port, etc.
        
        //Finished loading
        try {
            registryConnection.agentLoaded(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPerformance() {
        System.out.println("Human is performing");
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.HumanPerformer;
    }

    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        return "I am human!";
    }
}
