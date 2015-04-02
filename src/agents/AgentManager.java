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
        System.out.println("Launching the agent container ... " + pContainer);
        jade.wrapper.AgentContainer cont = rt.createAgentContainer(pContainer);
        System.out.println("Launching the agent container after ... " + pContainer);

        System.out.println("Containers created");

        try {
            System.out.println("Launching the studio agent on the main container ...");
            AgentController studioAgent = mainContainer.createNewAgent("Studio",
                    "agents.Studio", agentArgs);
            studioAgent.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
