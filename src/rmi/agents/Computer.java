/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Arrays;
import rmi.misc.AgentType;
import run.Main;

/**
 *
 * @author Martin
 */
public class Computer extends Agent {

    private File[] midiFiles;
    private File folder;

    public Computer(String name, String registryURL, String ip, int port, int servicePort, int masterMonitorID, File[] midiFiles) throws RemoteException {
        super(name, registryURL, ip, port, servicePort, masterMonitorID);

        //Get paths
        this.midiFiles = midiFiles;
        if (midiFiles.length != 0) {
            folder = midiFiles[0].getParentFile();
        }
    }

    @Override
    public void runBehaviour() {
        System.out.println("Computer is playing");
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.AIPerformer;
    }

    @Override
    public String getAgentTypeSpecificInfo() throws RemoteException {
        String result = "I am AI agent!\n";
        if ((midiFiles != null) && (folder != null)) {
            result += "Files: " + Arrays.toString(midiFiles) + "\n"
                    + "Folder: " + folder.getPath() + "\n"
                    + "\n";
        }
        return result;
    }

}
