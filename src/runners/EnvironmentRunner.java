/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runners;

import agents.AgentManager;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Martin
 */
public class EnvironmentRunner {

    //Constants (should be customizable?
    private static final int port = 1201;

    public static void main(String[] args) {
        initializeJadeRuntime(port);
        createAgentManager(simusicContainer);
    }

    //JADE vars
    static jade.core.Runtime rt;
    static Profile profile;
    static jade.wrapper.AgentContainer mainContainer;
    static ProfileImpl profileImpl;
    static jade.wrapper.AgentContainer simusicContainer;
    static AgentController rma;

    private static void initializeJadeRuntime(int port) {
        String host = null;
        String platformID = null;

        //Start loading JADE
        rt = jade.core.Runtime.instance();
        rt.setCloseVM(true);
        System.out.println("JADE runtime created");
        profile = new ProfileImpl(host, port, platformID);
        System.out.println("JADE profile created");

        System.out.println("Launching a whole in-process platform..." + profile);
        mainContainer = rt.createMainContainer(profile);
        profileImpl = new ProfileImpl(host, port, platformID);
        profileImpl.setParameter(Profile.CONTAINER_NAME, "Simusic");
        System.out.println("Launching the agent container ... " + profileImpl);
        simusicContainer = rt.createAgentContainer(profileImpl);

        System.out.println("Containers created");

        try {
            System.out.println("Creating the RMA agent in the main container ...");
            rma = mainContainer.createNewAgent("rma",
                    "jade.tools.rma.rma", new Object[0]);

            System.out.println("Launching JADE RMA...");
            rma.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAgentManager(AgentContainer container) {
        try {
            System.out.println("Creating the AgentManager in the Simusic controller ...");
            rma = container.createNewAgent("AgentManager",
                    "agents.AgentManager", new Object[0]);

            System.out.println("Launching the AgentManager...");
            rma.start();
            System.out.println("AgentManager launched!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
