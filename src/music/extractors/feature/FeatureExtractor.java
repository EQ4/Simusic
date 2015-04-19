/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.extractors.feature;

import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jsymbolic.*;
import org.w3c.dom.*;

/**
 *
 * @author Martin
 */
public class FeatureExtractor {

    String midiPath;
    String featurePath;
    HashMap<String, ArrayList<Double>> mapOfSongListsOfFeatures;
    HashMap<String, Double> mapOfAverageFeatures;
    ArrayList<String> listOfSongNames;
    int numberOfSongs;

    boolean log = false;

    public FeatureExtractor(String midiPath, String featurePath, boolean overwrite) {
        this.midiPath = midiPath;
        this.featurePath = featurePath;
        this.mapOfSongListsOfFeatures = new HashMap<>();
        mapOfAverageFeatures = new HashMap<>();
        this.listOfSongNames = new ArrayList<>();
        this.numberOfSongs = 0;

        File folder = new File(midiPath);
        File[] files = folder.listFiles();

        if (log) {
            System.out.println("Extracting features from " + files.length + " files...");
        }
        int counter = 0;

        //Write loop
        for (File file : files) {

            //Ignore folders
            if (!file.isFile()) {
                continue;
            }

            try {
                //If not (overwrite), check if xml already exists
                if (!overwrite) {
                    File f = new File(featurePath + file.getName() + ".xml");
                    if (f.exists() && !f.isDirectory()) {
                        System.out.println("\t" + ++counter + "/" + files.length + " - XML already exsists");
                        continue;
                    }
                }

                System.out.println("\t" + ++counter + "/" + files.length);

                //Extract features to XML
                CommandLine.extractFeatures(
                        file.getCanonicalPath(),
                        featurePath + file.getName() + ".xml",
                        featurePath + file.getName() + "_def.xml", false);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //Read loop
        for (File file : files) {
            try {

                //Store features from XML to hashmap
                File xmlFile = new File(featurePath + file.getName() + ".xml");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (log) {
            System.out.println("Features extracted!");
        }
    }

    public double getFeatureValue(int songNumber, String featureName) {
        //return listOfSongMapsOfFeatures.get(songNumber).get(featureName);
        return mapOfSongListsOfFeatures.get(featureName).get(songNumber);
    }

    public double getAverageFeatureValue(String featureName) {
        //return listOfSongMapsOfFeatures.get(songNumber).get(featureName);
        return mapOfAverageFeatures.get(featureName);
    }

    public String getSongName(int songNumber) {
        return listOfSongNames.get(songNumber);
    }

    public void printFeature(int songNumber, String featureName) {
        System.out.println(featureName + " of " + getSongName(songNumber) + ": " + getFeatureValue(songNumber, featureName));
    }

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

    public void printAverageFeatures() {
        System.out.println("Printing average features:");
        for (Map.Entry<String, Double> entry : mapOfAverageFeatures.entrySet()) {
            System.out.println("\t" + entry.getKey() + " - " + entry.getValue());
        }
    }
}