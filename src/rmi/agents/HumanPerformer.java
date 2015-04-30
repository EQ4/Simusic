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

import java.rmi.RemoteException;
import enums.AgentType;
import enums.AuctionType;
import music.elements.Chord;
import music.extractors.feature.GlobalFeatureContainer;
import rmi.messages.AuctionMessage;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class HumanPerformer extends Agent {

    GlobalFeatureContainer globalFeatures;

    /**
     *
     * @param name
     * @param registryURL
     * @param ip
     * @param port
     * @param servicePort
     * @param masterMonitorID
     * @throws RemoteException
     */
    public HumanPerformer(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);
    }

    /**
     *
     */
    @Override
    public void loadAgent() {
        //TODO: Select MIDI USB port, etc.
        try {
            globalFeatures = registryConnection.getGlobalFeatures();
            registryConnection.agentLoaded(agentID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param initialTempo
     */
    @Override
    public void performanceStarted(int initialTempo) {
        log("Performance started at tempo " + initialTempo, false);
    }

    /**
     *
     */
    @Override
    public void performanceStopped() {
        log("Performance stopped", false);
    }

    /**
     *
     * @return
     */
    @Override
    public AgentType getAgentType() {
        return AgentType.HumanPerformer;
    }

    /**
     *
     * @return @throws RemoteException
     */
    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        return "I am human!";
    }

    /**
     *
     */
    @Override
    public void playSolo() {
        // Human plays all the time or...?
        // Idea: Human could steal the limelight
        // ...while agents listen to the human performance and learn (patterns, tempo, dynamics, etc).
    }

    /**
     *
     * @param chord
     * @throws RemoteException
     */
    @Override
    public void beat(Chord chord) throws RemoteException {
        // Humans can play chords on a guitar without having to call their void methods Now.
    }

    /**
     *
     * @param featureName
     * @return
     * @throws RemoteException
     */
    @Override
    public Double getAverageFeature(String featureName) throws RemoteException {
        //Not Applicable
        return (double) -1;
    }

    /**
     *
     * @param auctionType
     * @param args
     * @return
     * @throws RemoteException
     */
    @Override
    public AuctionMessage executeLocalAuction(AuctionType auctionType, String[] args) throws RemoteException {
        //Not Applicable
        return null;
    }

    /**
     *
     * @param neighbourID
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean connectNeighbour(int neighbourID) throws RemoteException {
        //Not applicable
        return false;
    }
}
