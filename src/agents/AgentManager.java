/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JFrame;
import run.Main;
import run.Tests;

/**
 *
 * @author Martin
 */
public class AgentManager extends Agent {

    String studioGUID;
    String performersFolder;
    int maxMarkovHarmonyLevel;
    Main mainFrame;

    private void executeUsingNewStudio() {
        //Create new studio

        try {
            System.out.println("<AgentManager> Creating the Studio agent in the main container...");
            AgentController studioAgent = getContainerController().createNewAgent("Studio",
                    "agents.Studio", new Object[]{getName()});
            System.out.println("<AgentManager> Launching the Studio...");
            studioAgent.start();
            System.out.println("<AgentManager> Studio agent launched. Studio GUID: " + studioAgent.getName());
            studioGUID = studioAgent.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create local performers and wait for remote ones to join
        createPerformers(performersFolder, studioGUID);

        mainFrame.haltSimulation("Waiting for remote performers to join...");

        //Once ready, send load (and lock session) command to Studio
        send(agents.Services.SendMessage(studioGUID, "do_load_performers"));

        //TODO: Runtime GUI
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    String msg = incomingMessage.getContent();
                    if (msg.contains("report_performers_loaded")) {
                        System.out.println("<AgentManager> Performance stage reached!");
                        send(agents.Services.SendMessage(studioGUID, "do_start_performance"));
                    }
                }
            }
        });
    }

    void executeUsingRemoteStudio() {
        createPerformers(performersFolder, studioGUID);
    }

    @Override
    protected void setup() {
        
        //Get studio address from args (empty if creating new)
        Object[] agentArgs = getArguments();
        studioGUID = (String) agentArgs[0];
        mainFrame = (Main)agentArgs[1];

        //TODO: Configure GUI
        maxMarkovHarmonyLevel = 4;
        performersFolder = "E:\\Desktop\\Dissertation\\_agents\\";
        
        if (studioGUID.isEmpty()) {
            executeUsingNewStudio();
        } else {
            executeUsingRemoteStudio();
        }

        System.out.println("<AgentManager> Hello World!");
    }

    public void createPerformers(String path, String studioAgent) {

        FileFilter directoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        File mainFolder = new File(path);
        File[] agentFolders = mainFolder.listFiles(directoryFilter);

        for (int i = 0; i < agentFolders.length; i++) {
            try {
                String maxMarkovLevel = maxMarkovHarmonyLevel + "";
                Object[] agentArgs = new Object[]{agentFolders[i].getPath(), studioAgent, getName(), (String) maxMarkovLevel};
                AgentController newPerformer = getContainerController().createNewAgent(agentFolders[i].getName(), "agents.Performer", agentArgs);
                newPerformer.start();
                System.out.println("<AgentManager> Performer " + newPerformer.getName() + " has been created");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
