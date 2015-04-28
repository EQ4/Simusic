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
package rmi.agents.monitor;

import enums.AuctionType;
import rmi.messages.UpdateMessage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import music.elements.Chord;
import music.elements.Playable;
import rmi.interfaces.AgentInterface;
import rmi.interfaces.RegistryInterface;
import rmi.messages.AuctionMessage;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class MonitorDaemon extends UnicastRemoteObject implements AgentInterface {

    MonitorFrame frame;

    /**
     *
     * @param frame
     * @throws RemoteException
     */
    public MonitorDaemon(MonitorFrame frame) throws RemoteException {
        super(frame.monitorServicePort);
        this.frame = frame;
    }

    //AI methods

    /**
     *
     * @param message
     * @param senderID
     * @throws RemoteException
     */
        @Override
    public void unicast(String message, int senderID) throws RemoteException {
        frame.log("Agent " + senderID + " told me '" + message + "'", false);
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    //Monitor methods

    /**
     *
     * @return
     * @throws RemoteException
     */
        @Override
    public String sayHello() throws RemoteException {
        frame.log("Registry says hi!", false);
        return "Hi from agent!";
    }

    /**
     *
     * @param update
     * @throws RemoteException
     */
    @Override
    public void update(UpdateMessage update) throws RemoteException {
        frame.log("Monitor view updated", false);
        frame.processUpdate(update);
    }

    /**
     *
     * @param optimisticAgentID
     * @return
     */
    @Override
    public boolean connectNeighbour(int optimisticAgentID) {
        frame.log("Agent #" + optimisticAgentID + " is trying to neighbour a monitor.", false);
        return false;
    }

    /**
     *
     * @param neighbourID
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean disconnectNeighbour(int neighbourID) throws RemoteException {
        frame.log("Impossible!", false);
        return false;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean shutdown() throws RemoteException {
        //Simply disconnect from registry
        frame.disconnect();
        frame.log("Registry has shut itself down.", false);
        return false;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        //Used mainly in Human and AI performers
        return "I am monitor!";
    }

    /**
     *
     * @param initialTempo
     * @throws RemoteException
     */
    @Override
    public void performanceStarted(int initialTempo) throws RemoteException {
        frame.log("Performance started at tempo " + initialTempo, false);
        frame.setPerformingStatus(true);
    }

    /**
     *
     * @throws RemoteException
     */
    @Override
    public void performanceStopped() throws RemoteException {
        frame.log("Performance stopped", false);
        frame.setPerformingStatus(false);
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
        //Monitor auctions... No idea what they'd be useful for
        return null;
    }

    /**
     *
     * @param chord
     * @throws RemoteException
     */
    @Override
    public void beat(Chord chord) throws RemoteException {
        frame.log("Current chord: " + chord.toString() + " by agent #" + chord.agentID + ", probability: " + chord.getProbability() + (chord.isMutated ? " MUTATED!" : ""), true);
    }

    /**
     *
     * @throws RemoteException
     */
    @Override
    public void playSolo() throws RemoteException {
        //Not Applicable
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
     * @throws RemoteException
     */
    @Override
    public void loadAgent() throws RemoteException {
        //Not applicable
    };
}
