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
import test.RunTests;

/**
 *
 * @author Martin
 */
public class Studio extends Agent {

    String[] performerGUIDs;
    int performerCounter = 0;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        FileFilter directoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File mainFolder = new File((String) args[0]);
        File[] folders = mainFolder.listFiles(directoryFilter);

        performerGUIDs = new String[folders.length];
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
                }
            }
        });

        loadPerformer(performerCounter++);

    }

    void loadPerformer(int performer) {
        if (performer < performerGUIDs.length) {
            System.out.println("Studio: Starting " + performerGUIDs[performer] + "...");
            send(agents.Services.SendMessage(performerGUIDs[performer], "load_yourself"));
        } else {
            System.out.println("Studio: All performers have finished loading.");
            
            //Once all performers have loaded, call:
            startSession();
        }
    }

    void startSession() {
        //Play simultaneous clicks
        for (int i = 0; i < 12; i++) {
            clickTest();
            wait(1000);
        }
    }

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

    void wait(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
