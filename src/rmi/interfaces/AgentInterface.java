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

import enums.AuctionType;
import java.rmi.Remote;
import java.rmi.RemoteException;
import music.elements.Chord;
import rmi.messages.AuctionMessage;
import rmi.messages.UpdateMessage;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public interface AgentInterface extends Remote  {
    //AI methods

    /**
     *
     * @param agentID
     * @return
     * @throws RemoteException
     */
        public boolean connectNeighbour(int agentID) throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean ping() throws RemoteException;

    /**
     *
     * @param update
     * @throws RemoteException
     */
    public void update(UpdateMessage update) throws RemoteException;

    /**
     *
     * @param message
     * @param senderID
     * @throws RemoteException
     */
    public void unicast(String message, int senderID) throws RemoteException;

    /**
     *
     * @throws RemoteException
     */
    public void loadAgent() throws RemoteException;

    /**
     *
     * @param agentID
     * @return
     * @throws RemoteException
     */
    public boolean disconnectNeighbour(int agentID) throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean shutdown() throws RemoteException;

    /**
     *
     * @param auctionType
     * @param args
     * @return
     * @throws RemoteException
     */
    public AuctionMessage executeLocalAuction(AuctionType auctionType, String[] args) throws RemoteException;

    /**
     *
     * @param featureName
     * @return
     * @throws RemoteException
     */
    public Double getAverageFeature(String featureName) throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public abstract String getAgentTypeSpecificInfo() throws RemoteException;
    
    /**
     *
     * @param initialTempo
     * @throws RemoteException
     */
    public void performanceStarted(int initialTempo) throws RemoteException;

    /**
     *
     * @throws RemoteException
     */
    public void performanceStopped() throws RemoteException;
    
    /**
     *
     * @param chord
     * @throws RemoteException
     */
    public void beat(Chord chord) throws RemoteException;

    /**
     *
     * @throws RemoteException
     */
    public void playSolo() throws RemoteException;
    
    
    //Test

    /**
     *
     * @return
     * @throws RemoteException
     */
        public String sayHello() throws RemoteException;
}
