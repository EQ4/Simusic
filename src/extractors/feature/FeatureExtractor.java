/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extractors.feature;

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
    ArrayList<String> listOfSongNames;
    int numberOfSongs;

    public FeatureExtractor(String midiPath, String featurePath) {
        this.midiPath = midiPath;
        this.featurePath = featurePath;
        this.mapOfSongListsOfFeatures = new HashMap<>();
        this.listOfSongNames = new ArrayList<>();
        this.numberOfSongs = 0;

        File folder = new File(midiPath);
        File[] files = folder.listFiles();


        System.out.println("Extracting features from " + files.length + " files...");
        int counter = 0;
        
        
        for (File file : files) {

            if (!file.isFile()) {
                continue;
            }
            
            System.out.println("\t" + ++counter + "/" + files.length);

            try {

                //Extract features to XML
                CommandLine.extractFeatures(
                        file.getCanonicalPath(),
                        featurePath + file.getName() + ".xml",
                        featurePath + file.getName() + "_def.xml", false);

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

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
        
        System.out.println("Done!");
    }

    public double getFeatureValue(int songNumber, String featureName) {
        //return listOfSongMapsOfFeatures.get(songNumber).get(featureName);
        return mapOfSongListsOfFeatures.get(featureName).get(songNumber);
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

    public HashMap<String, Double> computeAverageFeatures() {
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<String, ArrayList<Double>> entry : mapOfSongListsOfFeatures.entrySet()) {
            result.put(entry.getKey(), calculateAverage(entry.getValue()));
        }
        return result;
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
        HashMap<String, Double> averageMap = computeAverageFeatures();
        for (Map.Entry<String, Double> entry : averageMap.entrySet()) {
            System.out.println("\t" + entry.getKey() + " - " + entry.getValue());
        }
    }
}
