/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import jade.core.*;
import jade.wrapper.AgentController;

/**
 *
 * @author Martin
 */
public class AgentManager {

    public AgentManager(String agentsPath) {
        int port = 1200;
        String host = null;
        String platformID = null;
        Object[] agentArgs = new Object[]{agentsPath};


        //Start loading JADE
        jade.core.Runtime rt = jade.core.Runtime.instance();
        rt.setCloseVM(true);
        System.out.println("JADE runtime created");
        Profile profile = new ProfileImpl(host, port, platformID);
        System.out.println("JADE profile created");

        System.out.println("Launching a whole in-process platform..." + profile);
        jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);
        ProfileImpl pContainer = new ProfileImpl(host, port, platformID);
        pContainer.setParameter(Profile.CONTAINER_NAME, "Simusic");
        System.out.println("Launching the agent container ... " + pContainer);
        jade.wrapper.AgentContainer studioContainer = rt.createAgentContainer(pContainer);

        System.out.println("Containers created");

        try {
            System.out.println("Launching the rma agent on the main container ...");
            AgentController rma = mainContainer.createNewAgent("rma",
                    "jade.tools.rma.rma", new Object[0]);

            System.out.println("Launching the studio agent on the main container ...");
            AgentController studioAgent = studioContainer.createNewAgent("Studio",
                    "agents.Studio", agentArgs);
            
            System.out.println("Starting agents...");
            rma.start();
            studioAgent.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
