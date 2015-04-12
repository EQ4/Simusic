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
import runners.EnvironmentRunner;
import runners.TestRunner;

/**
 *
 * @author Martin
 */
public class AgentManager extends Agent {

    //Constants (should be customizable?
    private static final int maxMarkovHarmonyLevel = 4;
    
    private static final String performersFolder = "E:\\Desktop\\Dissertation\\_agents\\";
    private static final String remoteStudio = "";

    String studioGUID;

    void executeUsingNewStudio() {
        //Command line: ------------------------------^
        studioGUID = createStudio();
        createPerformers(performersFolder, studioGUID);

        System.out.println("<AgentManager> Waiting for remote performers to join...");
        Services.wait(5000);
        
        send(agents.Services.SendMessage(studioGUID, "do_load_performers"));

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

    void executeUsingExistingStudio(String studioGUID) {
        createPerformers(performersFolder, studioGUID);
    }

    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                if (remoteStudio.isEmpty()) {
                    executeUsingNewStudio();
                }
                else {
                    executeUsingExistingStudio(remoteStudio);
                }
            }
        });

        System.out.println("<AgentManager> Hello World!");
    }

    public String createStudio() {
        String studioGUID = null;
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
        return studioGUID;
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
