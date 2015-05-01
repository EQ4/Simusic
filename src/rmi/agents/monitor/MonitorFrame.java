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
package rmi.agents.monitor;

import rmi.messages.UpdateMessage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import enums.AgentType;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.text.DefaultCaret;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;
import music.player.Player;
import rmi.agents.Agent;
import rmi.agents.AIPerformer;
import rmi.agents.HumanPerformer;
import rmi.interfaces.AgentInterface;
import rmi.interfaces.RegistryInterface;
import rmi.agents.registry.NewRegistryDialog;
import run.Main;

/**
 * Main Monitor frame
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class MonitorFrame extends javax.swing.JFrame implements Runnable {

    static final int NEW_AGENT_INITIALIZE_TIME = 200;
    public RegistryInterface registryConnection;
    public String registryURL;
    public Integer monitorID;
    public int updateDelay;
    public String monitorRMIAddress;
    public String selectedIPInterface;
    public String monitorName;
    public int monitorPort;
    public int monitorServicePort;
    public MonitorDaemon monitorDaemon;
    public java.rmi.registry.Registry rmiRegistryLocation;
    public HashMap<Integer, AgentInterface> spawnedAgentConnections;

    private DefaultCaret logCaret;

    private MidiDevice.Info[] midiInfos;

    /**
     * This method is called by a second thread (not the main frame thread) to
     * spawn the Monitor Daemon
     */
    @Override
    public void run() {
        try {
            monitorDaemon = new MonitorDaemon(this);
            rmiRegistryLocation = java.rmi.registry.LocateRegistry.createRegistry(monitorPort);
            monitorRMIAddress = "rmi://" + selectedIPInterface + ":" + monitorPort + "/" + monitorName;
            Naming.rebind(monitorRMIAddress, monitorDaemon);

            setTitle("Simusic Monitor '" + monitorName + "'");
            log("--- SiMusic Monitor Log ---\nMonitor information: "
                    + "\n    - name: " + monitorName
                    + "\n    - ip: " + selectedIPInterface
                    + "\n    - port " + monitorPort
                    + "\n    - s. port " + monitorServicePort
                    + "\n", false);
            statusTextField.setText("Idle (not connected to a Registry)");
        } catch (ExportException e) {
            if (e.getMessage().contains("already in use")) {
                alert("Port is already in use!");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Default constructor, called by Main
     */
    public MonitorFrame() {
        initComponents();
        Main.windowsCurrentlyOpened++;

        //Set some values
        monitorPortField.setText(Main.getRandomPort() + "");
        monitorServicePortField.setText(Main.getRandomPort() + "");

        //Set up controls
        ipCombo.setModel(new DefaultComboBoxModel(getAllIPs()));
        //TO IMPLEMENT: Midi Synth Picker
        //midiCombo.setModel(new DefaultComboBoxModel(getAllMIDIs()));

        monitorNameField.setText(Main.getRandomName());

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        logCaret = (DefaultCaret) logField.getCaret();
        logCaret.setUpdatePolicy(ALWAYS_UPDATE);

        spawnedAgentConnections = new HashMap<>();

    }

    /**
     * Ping Registry
     * @return @throws RemoteException
     */
    public boolean pingRegistry() throws RemoteException {
        if (registryConnection == null) {
            return false;
        }
        return registryConnection.ping(monitorID);
    }

    /**
     *
     * @return @throws RemoteException
     */
    public boolean isAssignedID() throws RemoteException {
        return (monitorID != null);
    }

    /**
     *
     * @return @throws RemoteException
     */
    public boolean checkConnection() throws RemoteException {
        boolean truthValue = (pingRegistry() && isAssignedID());
        if (truthValue) {
            startNewRegistryBtn.setEnabled(false);
            discBtn.setEnabled(true);
            connectBtn.setEnabled(false);
        } else {
            canvas.setViewport(null);
            canvas.revalidate();
            registryURL = null;
            startNewRegistryBtn.setEnabled(true);
            discBtn.setEnabled(false);
            connectBtn.setEnabled(true);
            statusTextField.setText("Disconnected");
            monitorIdTextField.setText("");
            agentsMenu.setEnabled(false);
            performanceMenu.setEnabled(false);
            log("Disconnected", false);
            revalidate();
        }
        return truthValue;
    }

    private void startNewRegistry() throws RemoteException {
        NewRegistryDialog d = new NewRegistryDialog(this, true, selectedIPInterface);
        if (!d.getResult()) {
            return;
        }
        Main.startLocalRegistryDaemon(selectedIPInterface, d.getRegName(), d.getRegPort(), d.getRegServicePort());
        log("Registry spawned: "
                + "\n    - name: " + d.getRegName()
                + "\n    - ip: " + selectedIPInterface
                + "\n    - port " + d.getRegPort()
                + "\n    - s. port " + d.getRegServicePort()
                + "\n    - address: " + monitorRMIAddress, false
        );

        Main.wait(NEW_AGENT_INITIALIZE_TIME);

        //Also connect
        connectToRegistry("rmi://" + selectedIPInterface + ":" + d.getRegPort() + "/" + d.getRegName());
    }

    private void connectToRegistry(String registryURL) throws RemoteException {
        log("Connecting to " + registryURL, false);
        try {
            registryConnection = (RegistryInterface) Naming.lookup(registryURL);
        } catch (Exception e) {
            log("Could not connect to Registry. Look at console for exception stack trace", false);
            alert("Could not connect to Registry. Look at console for exception stack trace");
            e.printStackTrace();
        }

        UpdateMessage firstUpdate = registryConnection.connect(AgentType.Monitor, monitorName, selectedIPInterface, monitorPort, null);

        if (firstUpdate == null) {
            alert("Registry has already started performance. Cannot connect.");
            return;
        }

        //Unpack and process update
        monitorID = firstUpdate.welcomePack.agentID;
        processUpdate(firstUpdate);

        monitorIdTextField.setText(monitorID + "");

        if (checkConnection()) {
            log("Connected!", false);
            statusTextField.setText("Connected to " + registryURL);
            monitorIdTextField.setText(monitorID.toString());
            agentsMenu.setEnabled(true);
            performanceMenu.setEnabled(true);
            this.registryURL = registryURL;
        }

        registryConnection.sayHello(monitorName);
    }

    private void connectToRegistry() throws RemoteException {
        String registryURL = (String) JOptionPane.showInputDialog(
                this,
                "Enter registry URL",
                "Connect to existing registry",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "rmi://");
        if ((registryURL == null) || registryURL.isEmpty()) {
            return;
        }

        connectToRegistry(registryURL);
    }

    private void addAiPerformer() throws RemoteException {
        createAgent(AgentType.AIPerformer);
    }

    private void addHumanPerformer() throws RemoteException {
        createAgent(AgentType.HumanPerformer);
    }

    private void createAgent(AgentType agentType) throws RemoteException {
        try {
            if (registryConnection.isPerforming()) {
                alert("Registry has already started performance. Cannot create agent.");
                return;
            }
            String name = (String) JOptionPane.showInputDialog(
                    this,
                    "Enter new agent name",
                    "Creating new agent...",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    Main.getRandomName());
            if (name == null) {
                return;
            }
            Agent newAgent;
            if (agentType == AgentType.AIPerformer) {
                //Load midi files
                File[] agentFiles;
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setDialogTitle("Select repertoire MIDI files");
                FileFilter fileFilter = new FileNameExtensionFilter("Midi Files (*.mid, *.midi)", "mid", "midi");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setCurrentDirectory(Main.getRuntimeDir());
                int fcReturnVal = fileChooser.showOpenDialog(this);
                if (fcReturnVal == JFileChooser.APPROVE_OPTION) {
                    agentFiles = fileChooser.getSelectedFiles();
                } else {
                    return;
                }

                //Get Markov level
                String maxMarkovChainLevel = (String) JOptionPane.showInputDialog(this,
                        "Specify maximum Markov chain order\nI.e. agent memory span\n(press OK if unsure)",
                        "Creating new agent...",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        Main.DEFAULT_MAX_MARKOV_ORDER);
                if (maxMarkovChainLevel == null) {
                    return;
                }

                //Create agent
                newAgent = new AIPerformer(name, registryURL, selectedIPInterface, Main.getRandomPort(), Main.getRandomPort(), monitorID, agentFiles, Integer.parseInt(maxMarkovChainLevel));
            } else {
                //TODO: Select USB MIDI interface

                //Create agent
                newAgent = new HumanPerformer(name, registryURL, selectedIPInterface, Main.getRandomPort(), Main.getRandomPort(), monitorID);
            }
            newAgent.start();
            //Wait for agent to initialize
            Main.wait(NEW_AGENT_INITIALIZE_TIME);
            spawnedAgentConnections.put(newAgent.agentID, (AgentInterface) Naming.lookup(newAgent.agentRmiAddress));
        } catch (ExportException e) {
            if (e.getMessage().contains("already in use")) {
                alert("Port is already in use!");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAbout() {
        String readmeContent = "README file not found!";
        try {
            readmeContent = new Scanner(new File("README.txt")).useDelimiter("\\Z").next();
            log(readmeContent, false);
        } catch (FileNotFoundException e) {
            System.out.println(readmeContent);
        }
        log(readmeContent, false);
    }

    private void alert(String message) {
        if (message != null) {
            JOptionPane.showMessageDialog(this, message);
        }
    }

    /**
     *
     * @param message
     * @param precise
     */
    public void log(String message, boolean precise) {
        logField.append(Main.getCurrentTimestamp(precise) + message + "\n");
    }

    private void test() throws RemoteException {
        alert("Testing");
    }

    /**
     *
     * @param status
     */
    public void setPerformingStatus(boolean status) {
        startPerformanceItem.setEnabled(!status);
        stopPerformanceItem.setEnabled(status);

        if (status) {
            statusTextField.setText("Performing at " + registryURL);
            log("Started performing in " + registryURL, false);
        } else {
            statusTextField.setText("Not performing at " + registryURL);
            log("Stopped performing in " + registryURL, false);
        }
    }

    private String[] getAllIPs() {
        ArrayList<String> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    String ipString = inetAddress.toString();
                    //Filter loopback and IPv6 addresses
                    if (!ipString.contains(":") && !ipString.contains("127.0.0.1")) {
                        result.add(ipString.replace("/", "").trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error resolving own IPs");
            e.printStackTrace();
        }
        return result.toArray(new String[result.size()]);
    }

    private String[] getAllMIDIs() {
        ArrayList<String> result = new ArrayList<>();
        try {
            // Obtain information about all the installed synthesizers.
            MidiDevice device = null;
            midiInfos = MidiSystem.getMidiDeviceInfo();
            for (int i = 0; i < midiInfos.length; i++) {
                try {
                    device = MidiSystem.getMidiDevice(midiInfos[i]);
                } catch (MidiUnavailableException e) {
                    // Handle or throw exception...
                }
                if (device instanceof Synthesizer) {
                    result.add(midiInfos[i].getDescription() + " " + midiInfos[i].getName());
                }
            }
        } catch (Exception e) {
            System.out.println("Error resolving own IPs");
            e.printStackTrace();
        }
        return result.toArray(new String[result.size()]);
    }

    private void setGlobalPlayerChoice() {
        /*
         for (int i = 0; i < midiInfos.length; i++) {
         try {
         String midiString = midiInfos[i].getDescription() + " " + midiInfos[i].getName();
         if (midiString.equals((String) midiCombo.getSelectedItem())) {
         Main.selectedMidiSynth = (Synthesizer) MidiSystem.getMidiDevice(midiInfos[i]);
         }

         } catch (MidiUnavailableException e) {
         e.printStackTrace();
         }
         }
         */
    }

    /**
     *
     * @throws RemoteException
     */
    public void disconnect() throws RemoteException {
        try {
            registryConnection.disconnect(monitorID);
        } catch (RemoteException e) {
            //Continue; connection is dead anyway
        }
        registryConnection = null;
        monitorID = null;
        checkConnection();
    }

    void startMonitor() throws RemoteException {
        startMonitorBtn.setText("Monitor started");
        startMonitorBtn.setEnabled(false);
        monitorNameField.setEditable(false);
        monitorPortField.setEditable(false);
        monitorServicePortField.setEditable(false);
        registryMenu.setEnabled(true);
        ipCombo.setEnabled(false);
        //midiCombo.setEnabled(false);

        //Set variables
        selectedIPInterface = (String) ipCombo.getSelectedItem();
        monitorName = monitorNameField.getText();
        monitorPort = Integer.parseInt(monitorPortField.getText());
        monitorServicePort = Integer.parseInt(monitorServicePortField.getText());

        // Set global MIDI synth
        // TODO: Set MIDI synth from dropdown menu
        // setGlobalPlayerChoice();
        try {
            Main.selectedMidiSynth = MidiSystem.getSynthesizer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create the local instance of Player
        Main.player = new Player();

        //Set RMI hostname system variable
        System.setProperty("java.rmi.server.hostname", selectedIPInterface);

        //Start monitor daemon
        Thread localMonitorWorker = new Thread(this);
        localMonitorWorker.setDaemon(true);
        localMonitorWorker.start();
    }

    void processUpdate(UpdateMessage update) {
        ArrayList<AgentIcon> agentIcons = new ArrayList<>();
        //Process dummies

        for (AgentDummy dummy : update.updatedDummies) {
            if (!dummy.isOffline) {
                try {
                    ImageIcon img = null;
                    try {
                        img = new ImageIcon(ImageIO.read(new File("resources/" + dummy.getIconFilename() + ".png")));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    AgentIcon icon = new AgentIcon(img, "<html><b>"
                            + dummy.name + "</b><br />"
                            + dummy.ip + ":"
                            + dummy.port + "<br />ID: "
                            + dummy.agentID
                            + ((dummy.masterMonitorID == null) ? "" : (", from monitor " + dummy.masterMonitorID))
                            + (dummy.isOffline ? "<br />OFFLINE" : "")
                            + (!dummy.isReady ? "<br /><em>AGENT LOADING</em>" : "<br /><strong>AGENT READY</strong>")
                            + "</html>", dummy.agentID) {
                                @Override
                                void iconClicked() {
                                    openAgentMenu(dummy);
                                }
                            };
                    icon.setOpaque(false);
                    agentIcons.add(icon);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        //Paint edges
        GridLayout newLayout = new GridLayout(0, 3, 32, 48);
        JPanel newPanel = new JPanel(newLayout) {
            @Override
            public void paintComponent(Graphics g) {

                Graphics2D newGraphics2D = (Graphics2D) g.create();
                newGraphics2D.setColor(java.awt.Color.BLACK);

                //Draw arrow base
                int rectangleBaseWidth = 200;
                int rectangleBaseHeight = 85;
                int rectXOffset = 110;
                int rectYOffset = 34;

                for (AgentIcon icon : agentIcons) {
                    AgentDummy updatedDummyOfIcon = update.updatedDummies.get(icon.agentIDNote);
                    if (updatedDummyOfIcon.isConductor) {
                        newGraphics2D.setColor(java.awt.Color.red);
                    } else if (updatedDummyOfIcon.isLeafAgent) {
                        newGraphics2D.setColor(java.awt.Color.green);
                    } else {
                        continue;
                    }
                    newGraphics2D.fill3DRect(icon.getX() + rectXOffset - (rectangleBaseWidth / 2), icon.getY() + rectYOffset - (rectangleBaseHeight / 2), rectangleBaseWidth, rectangleBaseHeight, true);
                }

                for (AgentDummyLink link : update.updatedLinks) {
                    AgentIcon iconFrom = null;
                    AgentIcon iconTo = null;
                    for (AgentIcon icon : agentIcons) {
                        if (icon.agentIDNote == link.fromAgentID) {
                            iconFrom = icon;
                        }
                        if (icon.agentIDNote == link.toAgentID) {
                            iconTo = icon;
                        }
                    }
                    if ((iconFrom == null) || (iconTo == null)) {
                        continue;
                    }
                    Point pointFrom = iconFrom.getLocation();
                    Point pointTo = iconTo.getLocation();
                    int x1 = pointFrom.x;
                    int y1 = pointFrom.y;
                    int x2 = pointTo.x;
                    int y2 = pointTo.y;

                    int minus_value = 50;
                    if (x2 < x1) {
                        x2 += minus_value;
                        x1 -= minus_value;
                    }
                    if (x2 > x1) {
                        x2 -= minus_value;
                        x1 += minus_value;
                    }
                    if (y2 < y1) {
                        y2 += minus_value;
                        y1 -= minus_value;
                    }
                    if (y2 > y1) {
                        y2 -= minus_value;
                        y1 += minus_value;
                    }

                    int max_jitter = 20;

                    x1 += 120 + Main.rand.nextInt(max_jitter);
                    x2 += 120 + Main.rand.nextInt(max_jitter);
                    y1 += 40 + Main.rand.nextInt(max_jitter);
                    y2 += 40 + Main.rand.nextInt(max_jitter);

                    //Following 10 lines taken from Stack Overflow
                    //Draw arrow head and line
                    newGraphics2D = (Graphics2D) g.create();
                    newGraphics2D.setColor(java.awt.Color.white);
                    final int ARR_SIZE = 14;

                    double dx = x2 - x1, dy = y2 - y1;
                    double angle = Math.atan2(dy, dx);
                    int len = (int) Math.sqrt(dx * dx + dy * dy);
                    AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
                    at.concatenate(AffineTransform.getRotateInstance(angle));
                    newGraphics2D.transform(at);

                    newGraphics2D.drawLine(0, 0, len, 0);
                    newGraphics2D.fillPolygon(new int[]{len, len - ARR_SIZE, len - ARR_SIZE, len},
                            new int[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);

                    //If agent is leaf, do not draw base.
                    if (update.updatedDummies.get(iconFrom.agentIDNote).isLeafAgent) {
                        continue;
                    }

                    //Draw arrow base
                    newGraphics2D = (Graphics2D) g.create();
                    newGraphics2D.setColor(java.awt.Color.ORANGE);
                    newGraphics2D.fill3DRect(pointFrom.x + rectXOffset - (rectangleBaseWidth / 2), pointFrom.y + rectYOffset - (rectangleBaseHeight / 2), rectangleBaseWidth, rectangleBaseHeight, true);

                }
            }
        };

        for (AgentIcon icon : agentIcons) {
            newPanel.add(icon);
        }

        pack();
        canvas.setViewportView(newPanel);
    }

    private void openAgentMenu(AgentDummy dummy) {
        AgentControlPanel newControlPanel = new AgentControlPanel(dummy, monitorID, registryConnection);
        newControlPanel.setLocationRelativeTo(this);
        new Thread(newControlPanel).start();
    }

    private void shutdown() throws RemoteException {
        int dialogResult = JOptionPane.showConfirmDialog(this, "Shutting down the monitor will also terminate any agents associated with this monitor.\n Are you sure you want to kill " + monitorNameField.getText() + "?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            for (AgentInterface agentConnection : spawnedAgentConnections.values()) {
                agentConnection.shutdown();
            }

            //Remove RMI naming
            try {
                rmiRegistryLocation.unbind(monitorName);
            } catch (Exception e) {
                System.out.println("Monitor RMI address already unbound");
            }

            Main.closeWindow(this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        logField = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        statusTextField = new javax.swing.JTextField();
        monitorIdTextField = new javax.swing.JTextField();
        monitorNameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        monitorPortField = new javax.swing.JTextField();
        monitorServicePortField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        startMonitorBtn = new javax.swing.JButton();
        ipCombo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        canvas = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        registryMenu = new javax.swing.JMenu();
        startNewRegistryBtn = new javax.swing.JMenuItem();
        connectBtn = new javax.swing.JMenuItem();
        discBtn = new javax.swing.JMenuItem();
        agentsMenu = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        addAiPerformerMenuItem = new javax.swing.JMenuItem();
        addHumanPerformerMenuItem = new javax.swing.JMenuItem();
        importAgentsFromFolderMenuItem = new javax.swing.JMenuItem();
        performanceMenu = new javax.swing.JMenu();
        startPerformanceItem = new javax.swing.JMenuItem();
        stopPerformanceItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        testMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenu2.setText("jMenu2");

        jMenuItem2.setText("jMenuItem2");

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SiMusic Monitor");
        setMinimumSize(new java.awt.Dimension(1000, 640));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        logField.setEditable(false);
        logField.setColumns(20);
        logField.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        logField.setRows(5);
        logField.setText("How to get started:\n1) Press 'Start Monitor' button\n2) Registry > Start new local registry\n3) Agents > Add...\n4) Performance > Start performance\n\n* Agent icons are clickable\n** Names and ports are randomly generated\n\n--------------------------------------------------\n\n");
        jScrollPane1.setViewportView(logField);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Status");

        jLabel2.setText("Monitor ID");

        statusTextField.setEditable(false);
        statusTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        statusTextField.setText("Disconnected");

        monitorIdTextField.setEditable(false);
        monitorIdTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N

        jLabel3.setText("Monitor Name");

        jLabel4.setText("RMI Port");

        jLabel5.setText("Service Port");

        startMonitorBtn.setText("Start Monitor!");
        startMonitorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMonitorBtnActionPerformed(evt);
            }
        });

        jLabel6.setText("Interface");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ipCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(monitorNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel4)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(monitorPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(monitorServicePortField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(monitorIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(startMonitorBtn))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(monitorIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(monitorNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(monitorPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(monitorServicePortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ipCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addComponent(startMonitorBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 28, Short.MAX_VALUE))
        );

        canvas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        registryMenu.setText("Registry");
        registryMenu.setEnabled(false);

        startNewRegistryBtn.setText("Start new local Registry...");
        startNewRegistryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startNewRegistryBtnActionPerformed(evt);
            }
        });
        registryMenu.add(startNewRegistryBtn);

        connectBtn.setText("Connect to existing Registry...");
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectBtnActionPerformed(evt);
            }
        });
        registryMenu.add(connectBtn);

        discBtn.setText("Disconnect");
        discBtn.setEnabled(false);
        discBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discBtnActionPerformed(evt);
            }
        });
        registryMenu.add(discBtn);

        jMenuBar1.add(registryMenu);

        agentsMenu.setText("Agents");
        agentsMenu.setEnabled(false);

        jMenu4.setText("Add...");

        addAiPerformerMenuItem.setText("AI Performer");
        addAiPerformerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAiPerformerMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(addAiPerformerMenuItem);

        addHumanPerformerMenuItem.setText("Human Performer");
        addHumanPerformerMenuItem.setToolTipText("");
        addHumanPerformerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHumanPerformerMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(addHumanPerformerMenuItem);

        agentsMenu.add(jMenu4);

        importAgentsFromFolderMenuItem.setText("Import AI Performers from folder");
        importAgentsFromFolderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importAgentsFromFolderMenuItemActionPerformed(evt);
            }
        });
        agentsMenu.add(importAgentsFromFolderMenuItem);

        jMenuBar1.add(agentsMenu);

        performanceMenu.setText("Performance");
        performanceMenu.setEnabled(false);

        startPerformanceItem.setText("Start");
        startPerformanceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startPerformanceItemActionPerformed(evt);
            }
        });
        performanceMenu.add(startPerformanceItem);

        stopPerformanceItem.setText("Stop");
        stopPerformanceItem.setEnabled(false);
        stopPerformanceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPerformanceItemActionPerformed(evt);
            }
        });
        performanceMenu.add(stopPerformanceItem);

        jMenuBar1.add(performanceMenu);

        helpMenu.setText("Help");

        testMenuItem.setText("Test!");
        testMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(testMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(canvas)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addAiPerformerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAiPerformerMenuItemActionPerformed
        try {
            addAiPerformer();
        } catch (RemoteException e) {
            log(e.getLocalizedMessage(), false);
            e.printStackTrace();
        }
    }//GEN-LAST:event_addAiPerformerMenuItemActionPerformed

    private void addHumanPerformerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHumanPerformerMenuItemActionPerformed
        try {
            addHumanPerformer();
        } catch (RemoteException e) {
            log(e.getLocalizedMessage(), false);
            e.printStackTrace();
        }
    }//GEN-LAST:event_addHumanPerformerMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        showAbout();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed
        try {
            test();
        } catch (RemoteException e) {
            log(e.getLocalizedMessage(), false);
            e.printStackTrace();
        }
    }//GEN-LAST:event_testMenuItemActionPerformed

    private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
        try {
            connectToRegistry();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_connectBtnActionPerformed

    private void startNewRegistryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startNewRegistryBtnActionPerformed
        try {
            startNewRegistry();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_startNewRegistryBtnActionPerformed

    private void discBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discBtnActionPerformed
        try {
            disconnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_discBtnActionPerformed

    private void startMonitorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMonitorBtnActionPerformed
        try {
            startMonitor();
        } catch (RemoteException e) {
            e.printStackTrace();
            startMonitorBtn.setEnabled(true);
            startMonitorBtn.setText("Start Monitor");
        }
    }//GEN-LAST:event_startMonitorBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            shutdown();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing

    private void startPerformanceItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startPerformanceItemActionPerformed
        try {
            alert(registryConnection.startPerformance());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_startPerformanceItemActionPerformed

    private void importAgentsFromFolderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importAgentsFromFolderMenuItemActionPerformed

        try {
            //Load midi files
            File agentFolders;
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select agent folder");
            fileChooser.setCurrentDirectory(Main.getRuntimeDir());
            int fcReturnVal = fileChooser.showOpenDialog(this);
            if (fcReturnVal == JFileChooser.APPROVE_OPTION) {
                agentFolders = fileChooser.getSelectedFile();
            } else {
                return;
            }

            //Get markov level
            String maxMarkovChainLevel = (String) JOptionPane.showInputDialog(this,
                    "Specify maximum Markov chain depth\n(press OK if unsure)",
                    "Importing agents...",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    Main.DEFAULT_MAX_MARKOV_ORDER);
            if (maxMarkovChainLevel == null) {
                return;
            }

            //Create agents
            if (agentFolders.isFile()) {
                return;
            }

            for (File agentFolder : agentFolders.listFiles()) {
                if (agentFolder.isDirectory()) {
                    ArrayList<File> midiFiles = new ArrayList<>();
                    for (File file : agentFolder.listFiles()) {
                        if (file.getName().contains(".mid")) {
                            midiFiles.add(file);
                        }
                    }
                    Agent newAgent = new AIPerformer(agentFolder.getName().replace(" ", ""), registryURL, selectedIPInterface, Main.getRandomPort(), Main.getRandomPort(), monitorID, midiFiles.toArray(new File[midiFiles.size()]), Integer.parseInt(maxMarkovChainLevel));
                    newAgent.start();
                    //Wait for agent to initialize
                    Main.wait(NEW_AGENT_INITIALIZE_TIME);
                    spawnedAgentConnections.put(newAgent.agentID, (AgentInterface) Naming.lookup(newAgent.agentRmiAddress));
                }
            }

        } catch (ExportException e) {
            if (e.getMessage().contains("already in use")) {
                alert("Port is already in use!");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_importAgentsFromFolderMenuItemActionPerformed

    private void stopPerformanceItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopPerformanceItemActionPerformed
        try {
            alert(registryConnection.stopPerformance());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_stopPerformanceItemActionPerformed

    private void logFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_logFieldFocusGained
        //Fixes log autoscroll bug
        logCaret = (DefaultCaret) logField.getCaret();
        logCaret.setUpdatePolicy(ALWAYS_UPDATE);
    }//GEN-LAST:event_logFieldFocusGained

    private void monitorIdTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monitorIdTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_monitorIdTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addAiPerformerMenuItem;
    private javax.swing.JMenuItem addHumanPerformerMenuItem;
    private javax.swing.JMenu agentsMenu;
    private javax.swing.JScrollPane canvas;
    private javax.swing.JMenuItem connectBtn;
    private javax.swing.JMenuItem discBtn;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem importAgentsFromFolderMenuItem;
    private javax.swing.JComboBox ipCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logField;
    private javax.swing.JTextField monitorIdTextField;
    private javax.swing.JTextField monitorNameField;
    private javax.swing.JTextField monitorPortField;
    private javax.swing.JTextField monitorServicePortField;
    private javax.swing.JMenu performanceMenu;
    private javax.swing.JMenu registryMenu;
    private javax.swing.JButton startMonitorBtn;
    private javax.swing.JMenuItem startNewRegistryBtn;
    private javax.swing.JMenuItem startPerformanceItem;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JMenuItem stopPerformanceItem;
    private javax.swing.JMenuItem testMenuItem;
    // End of variables declaration//GEN-END:variables
}
