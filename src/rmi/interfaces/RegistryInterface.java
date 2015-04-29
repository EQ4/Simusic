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
package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import rmi.messages.UpdateMessage;
import rmi.agents.registry.RegistryDaemon;
import enums.AgentType;
import music.extractors.feature.FeatureExtractor;
import music.extractors.feature.GlobalFeatureContainer;
import rmi.agents.monitor.AgentDummy;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public interface RegistryInterface extends Remote {
    
    
    public UpdateMessage connect(AgentType agentType, String agentName, String agentIP, int agentPort, Integer masterMonitorID) throws RemoteException;
    public boolean ping(int id) throws RemoteException;
    public AgentDummy getAgentDummyByID(int id) throws RemoteException;
    public AgentDummy getRoleModel(int agentID, Double[] featureValues) throws RemoteException;
    public void agentLoaded(int agentID) throws RemoteException;
    public void reportNeighbourConnection(int fromAgentID, int toAgentID) throws RemoteException;
    public String startPerformance() throws RemoteException;
    public String stopPerformance() throws RemoteException;
    public boolean isPerforming() throws RemoteException;
    public void log(String message, int loggerID) throws RemoteException;
    public boolean disconnect(int id) throws RemoteException;
    public String sayHello(String sender) throws RemoteException; 
    public GlobalFeatureContainer getGlobalFeatures() throws RemoteException; 
    
}
