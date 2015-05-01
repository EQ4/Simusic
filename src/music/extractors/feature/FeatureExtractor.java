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
package music.extractors.feature;

import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import rmi.agents.Agent;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class FeatureExtractor {

    File[] midiFiles;
    String featurePath;
    HashMap<String, ArrayList<Double>> mapOfSongListsOfFeatures;
    HashMap<String, Double> mapOfAverageFeatures;
    ArrayList<String> listOfSongNames;
    int numberOfSongs;

    /**
     *
     * @param midiFiles
     * @param featureFolder
     * @param overwrite
     * @param callingAgent
     */
    public FeatureExtractor(File[] midiFiles, File featureFolder, boolean overwrite, Agent callingAgent) {
        boolean agentIsLogging = (callingAgent != null);

        this.midiFiles = midiFiles;
        try {
            this.featurePath = featureFolder.getCanonicalPath() + "/";
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mapOfSongListsOfFeatures = new HashMap<>();
        mapOfAverageFeatures = new HashMap<>();
        this.listOfSongNames = new ArrayList<>();
        this.numberOfSongs = 0;

        if (agentIsLogging) {
            callingAgent.log("Extracting features from " + midiFiles.length + " files...", false);
        }

        int counter = 0;

        //Write loop
        for (File file : midiFiles) {

            try {
                if (file.isDirectory()) {
                    continue;
                }
                //If not (overwrite), check if xml already exists
                if (!overwrite) {
                    File f = new File(featurePath + file.getName() + ".xml");
                    if (f.exists() && !f.isDirectory()) {
                        if (agentIsLogging) {
                            System.out.println("Extracting features - file " + ++counter + "/" + midiFiles.length + " - XML already exsists");
                        }
                        continue;
                    }
                }

                if (agentIsLogging) {
                    callingAgent.log("Extracting features - file " + ++counter + "/" + midiFiles.length, false);
                }

                //Extract features to XML using jSymbolic
                jsymbolic.CommandLine.extractFeatures(
                        file.getCanonicalPath(),
                        featurePath + file.getName() + ".xml",
                        featurePath + file.getName() + "_def.xml", false);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //Read loop
        for (File file : midiFiles) {
            try {

                //Store features from XML to hashmap
                File xmlFile = new File(featurePath + file.getName() + ".xml");
                
                //First create XML parser instances
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                //The document...
                Document doc;

                try {
                    doc = dBuilder.parse(xmlFile);
                } catch (Exception e) {
                    continue;
                }

                //Extract features for single song:
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("feature");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    NodeList childList = nodeList.item(i).getChildNodes();

                    //Filter multi-dimension features
                    if (childList.getLength() != 5) {
                        continue;
                    }

                    Node nameNode = childList.item(1).getFirstChild();
                    Node valueNode = childList.item(3).getFirstChild();

                    String name = nameNode.getNodeValue();
                    Double value = Double.parseDouble(valueNode.getNodeValue());

                    //Store again
                    if (!mapOfSongListsOfFeatures.containsKey(name)) {
                        mapOfSongListsOfFeatures.put(name, new ArrayList<Double>());
                    }
                    mapOfSongListsOfFeatures.get(name).add(value);

                }

                //Store song name just for the record
                listOfSongNames.add(file.getName());

                //Increment song number
                numberOfSongs++;

                //Populate average feature hashmap
                for (Map.Entry<String, ArrayList<Double>> entry : mapOfSongListsOfFeatures.entrySet()) {
                    mapOfAverageFeatures.put(entry.getKey(), calculateAverage(entry.getValue()));
                }
                
                //Dispose of parsers
                dbFactory = null;
                dBuilder = null;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (agentIsLogging) {
            callingAgent.log("Features extracted!", false);
        }
    }

    /**
     *
     * @param songNumber
     * @param featureName
     * @return
     */
    public double getFeatureValue(int songNumber, String featureName) {
        //return listOfSongMapsOfFeatures.get(songNumber).get(featureName);
        return mapOfSongListsOfFeatures.get(featureName).get(songNumber);
    }

    /**
     *
     * @param featureName
     * @return
     */
    public double getAverageFeatureValue(String featureName) {
        //return listOfSongMapsOfFeatures.get(songNumber).get(featureName);
        return mapOfAverageFeatures.get(featureName);
    }

    /**
     *
     * @param songNumber
     * @return
     */
    public String getSongName(int songNumber) {
        return listOfSongNames.get(songNumber);
    }

    /**
     *
     * @param songNumber
     * @param featureName
     */
    public void printFeature(int songNumber, String featureName) {
        System.out.println(featureName + " of " + getSongName(songNumber) + ": " + getFeatureValue(songNumber, featureName));
    }

    /**
     *
     * @return
     */
    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    private Double calculateAverage(ArrayList<Double> list) {
        Double sum = Double.valueOf(0);
        if (!list.isEmpty()) {
            for (Double element : list) {
                sum += element;
            }
            return sum / list.size();
        }
        return sum;
    }

    /**
     *
     * @return
     */
    public String getAverageFeatures() {
        String result = "\n\n--Listing Average Features:\n";
        for (Map.Entry<String, Double> entry : mapOfAverageFeatures.entrySet()) {
            result += ("\t" + entry.getKey() + " - " + entry.getValue() + "\n");
        }
        return result + "\n";
    }

    /**
     *
     * @return
     */
    public Double[] getAverageFeatureValues() {
        return mapOfAverageFeatures.values().toArray(new Double[mapOfAverageFeatures.size()]);
    }

    /**
     *
     * @param featureName
     * @return
     */
    public boolean hasFeature(String featureName) {
        return mapOfAverageFeatures.containsKey(featureName);
    }
}
