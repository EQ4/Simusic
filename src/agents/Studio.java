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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import test.RunTests;

/**
 *
 * @author Martin
 */
public class Studio extends Agent {
    
    String[] performerGUIDs;
    List<Double> averageInitialTempos;
    int performerCounter;
    int initialTempo;
    int beatDelay;
    
    @Override
    protected void setup() {
        //Create agents from agent directory
        Object[] args = getArguments();
        FileFilter directoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File mainFolder = new File((String) args[0]);
        File[] folders = mainFolder.listFiles(directoryFilter);

        //Initialize vars
        performerGUIDs = new String[folders.length];
        averageInitialTempos = new LinkedList<>();
        
        AgentContainer c = getContainerController();
        for (int i = 0; i < folders.length; i++) {
            try {
                String maxMarkovLevel = RunTests.maxMarkovHarmonyLevel + "";
                Object[] agentArgs = new Object[]{folders[i].getPath(), (String) getName(), (String) maxMarkovLevel};
                AgentController a = c.createNewAgent(folders[i].getName(), "agents.Performer", agentArgs);
                a.start();
                performerGUIDs[i] = a.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Add message behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    String msg = incomingMessage.getContent();
                    if (msg.contains("load_finished")) {
                        loadPerformer(performerCounter++);
                    }
                    if (msg.contains("initial_tempo=")) {
                        averageInitialTempos.add(Double.parseDouble(msg.split("=")[1]));
                        //Check if last agent's tempo
                        if (averageInitialTempos.size() >= performerGUIDs.length) {
                            initialTempoReady();
                        }
                    }
                }
            }
        });

        //Start performer load chain
        performerCounter = 0;
        loadPerformer(performerCounter++);
        
    }
    
    void loadPerformer(int performer) {
        if (performer < performerGUIDs.length) {
            System.out.println("Studio: Starting " + performerGUIDs[performer] + "...");
            send(agents.Services.SendMessage(performerGUIDs[performer], "load_yourself"));
        } else {
            System.out.println("Studio: All performers have finished loading.");

            //Once all performers have loaded, call:
            getInitialTempo();
        }
    }
    
    void getInitialTempo() {
        performerCounter = 0;
        while (performerCounter < performerGUIDs.length) {
            send(agents.Services.SendMessage(performerGUIDs[performerCounter++], "get_initial_tempo"));
        }
    }
    
    void initialTempoReady() {
        initialTempo = (int)getAverageInitialTempo();
        beatDelay = 60000 / initialTempo;
        
        runInfiniteTest();
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
    
    void wait(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Print methods
    void printAverageInitialTempo() {
        System.out.print("Studio: Initial tempo is " + getAverageInitialTempo() + " (");
        ListIterator<Double> listIterator = averageInitialTempos.listIterator();
        while (listIterator.hasNext()) {
            System.out.print(listIterator.next() + ", ");
        }
        System.out.println(")");
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
            wait(1000);
        }
    }
    
    void runInfiniteTest() {
        while (true) {
            clickTest();
            wait(beatDelay/4);
        }
    }
}
