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
package rmi.agents.registry;

import static java.lang.Math.abs;
import rmi.messages.UpdateMessage;
import rmi.interfaces.RegistryInterface;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import java.rmi.RemoteException;
import java.util.ArrayList;
import rmi.agents.monitor.AgentDummy;
import rmi.interfaces.AgentInterface;
import run.Main;
import enums.AgentType;
import enums.AuctionType;
import enums.RegistryServiceType;
import java.util.HashMap;
import music.elements.Chord;
import music.extractors.feature.GlobalFeatureContainer;
import rmi.messages.AuctionMessage;
import rmi.agents.monitor.AgentDummyLink;
import rmi.agents.monitor.AgentDummyLink.AgentLinkType;

/**
 * The main Registry daemon
 * Responds to queries, etc.
 * @author Martin Minovski <martin at minovski.net>
 */
public class RegistryDaemon extends UnicastRemoteObject implements RegistryInterface {

    static final int LATENCY_PINGS_PER_AGENT = 10;
    static final int DELAY_BETWEEN_LATENCY_PINGS = 100;

    private final Object loadAgentLock = new Object();

    ArrayList<AgentDummy> featureDummies;
    RegistryFrame frame;

    int conductorAgentID = -1;
    AuctionMessage currentChordMessage;
    Integer currentSoloistID;

    boolean isPerforming;

    RegistryServiceType newServiceFlag;

    HashMap<String, Double> sessionFeatures;

    GlobalFeatureContainer globalFeatures;

    /**
     * Constructor
     * @param frame
     * @throws RemoteException
     */
    public RegistryDaemon(RegistryFrame frame) throws RemoteException {
        super(frame.registryServicePort);
        this.frame = frame;
        isPerforming = false;
        this.featureDummies = new ArrayList<>();
        globalFeatures = new GlobalFeatureContainer();
        populateSessionFeatures();
    }

    /**
     * Connect method - responds with a full Update message.
     * @param agentType
     * @param agentName
     * @param agentIP
     * @param agentPort
     * @param masterMonitorID
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized UpdateMessage connect(AgentType agentType, String agentName, String agentIP, int agentPort, Integer masterMonitorID) throws RemoteException {
        //If performance has started, no more agents can connect
        if (isPerforming) {
            return null;
        }

        int id = frame.agentDummies.size();
        AgentDummy newAgentDummy = new AgentDummy(agentType, agentName, id, agentIP, agentPort, masterMonitorID);
        AgentInterface agentConnection;
        try {
            agentConnection = (AgentInterface) Naming.lookup("rmi://" + agentIP + ":" + agentPort + "/" + agentName);
        } catch (Exception e) {
            System.out.println("Registry to agent connection exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        //Measure latency
        newAgentDummy.latency = measureAgentLatency(agentConnection);
        frame.agentDummies.add(newAgentDummy);

        frame.log("A new agent has connected!\n"
                + "    - name: " + agentName + "\n"
                + "    - type: " + agentType.toString() + "\n"
                + "    - ip: " + agentIP + "\n"
                + "    - port: " + agentPort + "\n"
                + "    - latency: " + newAgentDummy.latency + "\n"
                + "    - ID: " + id + "\n"
                + "    - owned by: " + masterMonitorID, false
        );

        //Say hello
        agentConnection.sayHello();

        //Store connection
        frame.agentConnections.add(agentConnection);

        //Update agents
        triggerGlobalUpdate();

        //Start loader thread
        //Only one agent loads at a time
        new Thread() {
            @Override
            public void run() {
                try {
                    loadAgentSync(id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return getFirstUpdate(newAgentDummy);
    }

    private void loadAgentSync(int agentID) throws RemoteException {
        //This method ensures that only 1 agent is being loaded at a time.
        synchronized (loadAgentLock) {
            frame.agentConnections.get(agentID).loadAgent();
        }
    }

    /**
     * Triggers global monitor update
     * @throws RemoteException
     */
    public synchronized void triggerGlobalUpdate() throws RemoteException {
        for (AgentInterface agent : frame.agentConnections) {
            if (agent != null) {
                agent.update(getRegularUpdate());
            }
        }
    }

    private UpdateMessage getRegularUpdate() {
        UpdateMessage result = new UpdateMessage(frame.agentDummies, frame.agentDummyLinks);
        return result;
    }

    private UpdateMessage getFirstUpdate(AgentDummy newAgentDummy) {
        UpdateMessage result = getRegularUpdate();
        result.welcomePack = newAgentDummy;
        return result;
    }

    private int measureAgentLatency(AgentInterface agentConnection) throws RemoteException {
        int latencySum = 0;
        long timeOnPingSend;
        for (int i = 0; i < LATENCY_PINGS_PER_AGENT; i++) {
            timeOnPingSend = System.currentTimeMillis();
            agentConnection.ping();
            latencySum += (System.currentTimeMillis() - timeOnPingSend);
            Main.wait(DELAY_BETWEEN_LATENCY_PINGS);
        }
        return (latencySum / LATENCY_PINGS_PER_AGENT) / 2;
    }

    /**
     * Ping service
     * @param id
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean ping(int id) throws RemoteException {
        frame.log("Agent " + id + " pinged and got ponged!", false);
        return true;
    }

    /**
     *
     * @return @throws RemoteException
     */
    @Override
    public boolean isPerforming() throws RemoteException {
        return isPerforming;
    }

    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean disconnect(int id) throws RemoteException {
        AgentDummy agent = frame.agentDummies.get(id);
        frame.agentConnections.remove(id);
        frame.log(agent.name + " (#" + id + ") has disconnected.", false);
        agent.isReady = true;
        agent.isOffline = true;

        //Update agents
        triggerGlobalUpdate();

        frame.log(agent.agentType + " " + agent.name + " has disconnected.", false);
        return true;
    }

    /**
     *
     * @param sender
     * @return
     * @throws RemoteException
     */
    @Override
    public String sayHello(String sender) throws RemoteException {
        frame.log(sender + " says hi!", false);
        return "Hello from registry!";
    }

    /**
     *
     * @param message
     * @param loggerID
     * @throws RemoteException
     */
    @Override
    public void log(String message, int loggerID) throws RemoteException {
        frame.log("<" + frame.agentDummies.get(loggerID).name + "> " + message, false);
    }

    /**
     *
     * @param agentID
     * @return
     */
    @Override
    public AgentDummy getAgentDummyByID(int agentID) {
        return frame.agentDummies.get(agentID);
    }

    /**
     *
     * @param fromAgentID
     * @param toAgentID
     * @throws RemoteException
     */
    @Override
    public void reportNeighbourConnection(int fromAgentID, int toAgentID) throws RemoteException {
        int newLinkID = frame.agentDummyLinks.size();
        AgentDummyLink newAgentLink = new AgentDummyLink(AgentLinkType.AINeighbourLink, newLinkID, fromAgentID, toAgentID);
        frame.agentDummyLinks.add(newAgentLink);

        //Update agents
        triggerGlobalUpdate();
    }

    /**
     *
     * @param agentID
     * @throws RemoteException
     */
    @Override
    public void agentLoaded(int agentID) throws RemoteException {
        frame.agentDummies.get(agentID).isReady = true;
        triggerGlobalUpdate();
    }

    /**
     *
     * @param agentID
     * @param featureValues
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized AgentDummy getRoleModel(int agentID, Double[] featureValues) throws RemoteException {
        AgentDummy roleModel = null;
        String roleModelMessage = "Agent #" + agentID + "'s role model info:\n";
        double minDistance = Double.MAX_VALUE;
        for (AgentDummy agent : featureDummies) {
            double distanceToAgent = 0;
            for (int i = 0; i < featureValues.length; i++) {
                distanceToAgent += abs(featureValues[i] - agent.features[i]);
            }
            if (distanceToAgent < minDistance) {
                minDistance = distanceToAgent;
                roleModel = agent;
            }
            roleModelMessage += "\tDistance to " + agent.name + ": " + distanceToAgent + "\n";
        }

        //Store role model message
        if (roleModel != null) {
            roleModel.roleModelMessage = roleModelMessage;
            frame.log(frame.agentDummies.get(agentID).name + "'s assigned role model is " + roleModel.name, false);
            frame.agentDummies.get(roleModel.agentID).isLeafAgent = false;
        } else {
            //Store conductor agent
            conductorAgentID = agentID;
            frame.log("Conductor agent is " + frame.agentDummies.get(conductorAgentID).name + " (#" + conductorAgentID + ")", false);
            frame.agentDummies.get(agentID).isConductor = true;
        }

        //Store this agent
        AgentDummy newRoleModel = frame.agentDummies.get(agentID);
        newRoleModel.features = featureValues;
        featureDummies.add(newRoleModel);
        return roleModel;
    }

    /**
     *
     * @return @throws RemoteException
     */
    @Override
    public String startPerformance() throws RemoteException {
        //Check if there is at least one AI agent
        for (int i = 0; i < frame.agentDummies.size(); i++) {
            if (frame.agentDummies.get(i).agentType == AgentType.AIPerformer) {
                //All good - continue
                break;
            }
            if (i == frame.agentDummies.size() - 1) {
                frame.log("Someone requested performance start, but there are no AI performers", false);
                return "There are no AI Performers!";
            }
        }

        //Ensure that no agent is loading.
        for (AgentDummy agentDummy : frame.agentDummies) {
            if (!agentDummy.isReady) {
                frame.log("Someone requested performance start, but one or more agents are still loading", false);
                return "One or more agents are still loading!";
            }
        }

        //TODO: Measure average latencies (just in case)
        //Idea: Also check that recursive ping duration doesn't exceed beat period
        globalFeatures.setCurrentTempo(getFeatureWithAuction("Initial Tempo").intValue());
        // Idea: Tempo could vary during runtime using service threads (concurrency issues ahead)

        //Ready to start performance services
        isPerforming = true;
        frame.log("Starting performance at tempo " + globalFeatures.getCurrentTempo() + "bpm, beat period: " + Main.getBeatPeriod(globalFeatures.getCurrentTempo()) + "[before beat loop]", true);

        for (AgentInterface agentConnection : frame.agentConnections) {
            agentConnection.performanceStarted(globalFeatures.getCurrentTempo());
        }

        //Start beat service
        newServiceFlag = RegistryServiceType.BeatAndHarmonyService;
        new Thread() {
            @Override
            public void run() {
                // Start beat cycle
                while (isPerforming) {
                    long beatInvokedTime = System.currentTimeMillis();
                    int currentBeatPeriod = Main.getBeatPeriod(globalFeatures.getCurrentTempo());

                    // Get next chord (TODO: don't change on every beat!)
                    try {
                        currentChordMessage = frame.agentConnections.get(conductorAgentID).executeLocalAuction(AuctionType.ChordAuction, null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        break;
                    }

                    // Broadcast next beat
                    for (AgentInterface agentConnection : frame.agentConnections) {
                        try {
                            Chord nextChord = new Chord(currentChordMessage.chordBase, currentChordMessage.chordMode);
                            nextChord.isDefault = currentChordMessage.isDefaultChord;
                            nextChord.agentID = currentChordMessage.chordOriginID;
                            nextChord.setProbability(currentChordMessage.chordProbability);
                            agentConnection.beat(nextChord);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    // Calculate time lost since beginning of loop cycle
                    int timeLost = (int) (System.currentTimeMillis() - beatInvokedTime);

                    // Log beat
                    /*
                     frame.log("@ BEAT: All beat signals sent!\n"
                     + "\tChord: " + currentChordMessage.chord.toString() + "\n"
                     + "\tBeat period time lost: " + timeLost + "/" + currentBeatPeriod,
                     true);
                     */
                    // Sleep remaining time from beat
                    int remainingSleepTime = ((4 * currentBeatPeriod) - timeLost);
                    if (remainingSleepTime < 0) {
                        frame.log("Performance forcibly interruupted due to current overall latency (" + timeLost + ") higher than beat period (" + currentBeatPeriod + ")", true);
                        try {
                            stopPerformance();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    Main.wait((int) remainingSleepTime);

                }
            }
        }.start();

        //Start melody service
        newServiceFlag = RegistryServiceType.MelodyService;
        new Thread() {
            @Override
            public void run() {
                // Start melody tokenizer
                while (isPerforming) {
                    try {
                        if (currentChordMessage == null) {
                            Main.wait(100);
                            continue;
                        }
                        //Flag current soloist
                        currentSoloistID = currentChordMessage.chordOriginID;
                        //Idea: gradually adjust global performance features to suit the solo
                        //IN A NEW THREAD!
                        adaptCommonFeaturesTo(currentChordMessage.chordOriginID);

                        //Log solo
                        String soloLogString = "@ " + frame.agentDummies.get(currentSoloistID).name + " is soloing!";
                        frame.log(soloLogString, true);
                        for (AgentInterface agentConnection : frame.agentConnections) {
                            agentConnection.unicast("TO-MONITORS-ONLY:" + soloLogString, -1);
                        }

                        //Play agent solo
                        frame.agentConnections.get(currentChordMessage.chordOriginID).playSolo();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //TODO: Start feature and dynamics services
        //Null means success
        return null;
    }

    /**
     *
     * @return @throws RemoteException
     */
    @Override
    public String stopPerformance() throws RemoteException {
        //Perform "can stop" check

        //This breaks the BeatHarmony service
        isPerforming = false;
        frame.log("Performance stopped!", true);
        for (AgentInterface agentConnection : frame.agentConnections) {
            agentConnection.performanceStopped();
        }

        //Null means success
        return null;
    }

    private Double getFeatureWithAuction(String featureName) throws RemoteException {
        AuctionMessage auctionResultMessage = frame.agentConnections.get(conductorAgentID).executeLocalAuction(AuctionType.FeatureAuction, new String[]{featureName});
        return auctionResultMessage.feature;
    }

    // TODO: IMPLEMENT SESSION FEATURE ENVIRONMENT
    private void populateSessionFeatures() {
        // TODO: must be customizable: using script?
        sessionFeatures = new HashMap<>();
        sessionFeatures.put("Maximum Note Duration", (double) 4);
    }

    private void adaptCommonFeaturesTo(int agentID) throws RemoteException {
        //TODO: Implement
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized GlobalFeatureContainer getGlobalFeatures() throws RemoteException {
        return globalFeatures;
    }

}
