/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.Services;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.wrapper.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import runners.TestRunner;
import static java.util.concurrent.TimeUnit.*;

/**
 *
 * @author Martin
 */
public class Studio extends Agent {

    private final int NUMBER_OF_PING_MESSAGES = 10;

    ArrayList<String> performerGUIDs;
    List<Double> averageInitialTempos;
    int performerCounter;
    int pingMessageCounter;
    long pingSendTimeFlag;
    String pongLog = "Performer pong log:\n";
    long[] pongBuffer = new long[NUMBER_OF_PING_MESSAGES];
    ArrayList<Long> averagePerformerLatencies;
    int initialTempo;
    int beatDelay;

    boolean sessionLocked = false;
    int connectedPerformers = 0;

    String managerGUID;

    @Override
    protected void setup() {

        //Initialize vars (we use folders.length as number of performers)
        performerGUIDs = new ArrayList<>();
        averageInitialTempos = new LinkedList<>();
        averagePerformerLatencies = new ArrayList<>();

        //Save manager GUID
        Object[] agentArgs = getArguments();
        managerGUID = (String) agentArgs[0];

        //Add message behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    String msg = incomingMessage.getContent();
                    if (!sessionLocked) {
                        if (msg.contains("join_request")) {
                            //TODO: Implement performer quota?
                            performerGUIDs.add(incomingMessage.getSender().getName());
                            connectedPerformers++;
                            //Acknowledge request
                            send(agents.Services.SendMessage(incomingMessage.getSender().getName(), "join_request_ack"));
                        }
                        if (msg.contains("do_load_performers")) {
                            sessionLocked = true;

                            //Start performer load chain
                            startLoadingPerformers();
                        }
                    } else {
                        if (msg.contains("do_start_performance")) {
                            startPerformance();
                        }
                        if (msg.contains("do_print_average_latencies")) {
                            printAverageLatencies();
                        }
                        if (msg.contains("load_finished")) {
                            //Once all agents have loaded, the loadPerformer calls next method in loading chain
                            loadPerformer();
                        }
                        if (msg.contains("pong")) {
                            long pingRoundTrip = System.currentTimeMillis() - pingSendTimeFlag;
                            pongBuffer[pingMessageCounter - 1] = pingRoundTrip;
                            pongLog += incomingMessage.getSender().getLocalName() + " pongs after " + pingRoundTrip + "\n";
                            pingPerformer();
                        }
                        if (msg.contains("initial_tempo=")) {
                            averageInitialTempos.add(Double.parseDouble(msg.split("=")[1]));
                            //Check if last agent's tempo
                            if (averageInitialTempos.size() >= connectedPerformers) {
                                initialTempoReady();
                            }
                        }
                    }
                }
            }
        });

        System.out.println("<Studio> Hello World! My manager is " + managerGUID);
    }

    void startLoadingPerformers() {
        performerCounter = 0;
        loadPerformer();
    }

    void loadPerformer() {
        if (performerCounter < connectedPerformers) {
            System.out.println("<Studio> Loading " + performerGUIDs.get(performerCounter) + "...");
            send(agents.Services.SendMessage(performerGUIDs.get(performerCounter), "load_yourself"));
            performerCounter++;
        } else {
            System.out.println("<Studio> All performers have finished loading.");

            //Once all performers have finished loading, call next method in chain:
            startPingingPerformers();
        }
    }

    void startPingingPerformers() {
        performerCounter = 0;
        pingMessageCounter = 0;
        pingPerformer();
    }

    void pingPerformer() {
        if (pingMessageCounter < NUMBER_OF_PING_MESSAGES) {
            System.out.println("<Studio> Pinging " + performerGUIDs.get(performerCounter) + "...");
            Services.wait(50);
            pingSendTimeFlag = System.currentTimeMillis();
            send(agents.Services.SendMessage(performerGUIDs.get(performerCounter), "ping"));
            pingMessageCounter++;
        } else {
            //Calculate avg latency for performer
            int latencySum = 0;
            for (int i = 0; i < NUMBER_OF_PING_MESSAGES; i++) {
                latencySum += (pongBuffer[i] / 2);
            }
            averagePerformerLatencies.add(Long.valueOf(latencySum));

            if (performerCounter < connectedPerformers - 1) {
                //Ping next performer
                pingMessageCounter = 0;
                performerCounter++;
                pingPerformer();
            } else {
                //Once done, print and continue chain:
                printAverageLatencies();
                getInitialTempo();
            }
        }
    }

    void getInitialTempo() {
        performerCounter = 0;
        while (performerCounter < connectedPerformers) {
            send(agents.Services.SendMessage(performerGUIDs.get(performerCounter++), "get_initial_tempo"));
        }
    }

    void initialTempoReady() {
        initialTempo = (int) getAverageInitialTempo();
        beatDelay = 60000 / initialTempo;
        System.out.println("<Studio> Initial tempo is " + initialTempo + " bpm with " + beatDelay + " ms delay between beats.");

        //FLAG: LOAD FINISHED --------------------------^
        reportLoadFinishedToManager();
    }

    void reportLoadFinishedToManager() {
        send(agents.Services.SendMessage(managerGUID, "report_performers_loaded"));
    }

    void startPerformance() {
        System.out.println("<Studio> Performing!");

        //FLAG: PERFORM FINISHED --------------------------^
    }

    public void schedleTicker(long delay) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(124);

        ScheduledFuture scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        clickTest();
                    }
                },
                delay,
                delay,
                TimeUnit.MILLISECONDS
        );
    }

    //Tool methods:
    double getAverageInitialTempo() {
        double sum = 0;
        ListIterator<Double> listIterator = averageInitialTempos.listIterator();
        while (listIterator.hasNext()) {
            sum += listIterator.next();
        }
        return (sum / (double) averageInitialTempos.size());
    }

    //Print methods
    void printAverageInitialTempo() {
        System.out.print("<Studio> Initial tempo is " + getAverageInitialTempo() + " (");
        ListIterator<Double> listIterator = averageInitialTempos.listIterator();
        while (listIterator.hasNext()) {
            System.out.print(listIterator.next() + ", ");
        }
        System.out.println(")");
    }

    void printAverageLatencies() {
        for (int i = 0; i < connectedPerformers; i++) {
            System.out.println("<Studio> Latency of " + performerGUIDs.get(i) + ": " + averagePerformerLatencies.get(i));
        }
    }

    //Test methods
    void clickTest() {
        for (String performer : performerGUIDs) {
            send(agents.Services.SendMessage(performer, "play_test_click"));
        }
    }

    void countTest() {
        for (String performer : performerGUIDs) {
            send(agents.Services.SendMessage(performer, "play_test_count"));
            Services.wait(1000);
        }
    }
}
