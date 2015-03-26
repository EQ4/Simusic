/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extractors.feature;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    ArrayList<HashMap<String, Double>> listOfSongMapsOfFeatures;
    ArrayList<String> listOfSongNames;
    int numberOfSongs;

    public FeatureExtractor(String midiPath, String featurePath) {
        this.midiPath = midiPath;
        this.featurePath = featurePath;
        this.listOfSongMapsOfFeatures = new ArrayList<>();
        this.listOfSongNames = new ArrayList<>();
        this.numberOfSongs = 0;

        File folder = new File(midiPath);
        File[] files = folder.listFiles();



        for (File file : files) {

            if (!file.isFile()) {
                continue;
            }

            try {
                //Initialize hashmap
                HashMap<String, Double> songFeatures = new HashMap<>();

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

                    //Print features
                    Node nameNode = childList.item(1).getFirstChild();
                    Node valueNode = childList.item(3).getFirstChild();

                    //Store value
                    songFeatures.put(nameNode.getNodeValue(), Double.parseDouble(valueNode.getNodeValue()));
                }

                //Store map
                listOfSongMapsOfFeatures.add(songFeatures);
                
                //Store song name just for the record
                listOfSongNames.add(file.getName());
                
                //Increment song number
                numberOfSongs++;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public double getFeatureValue(int songNumber, String featureName) {
        return listOfSongMapsOfFeatures.get(songNumber).get(featureName);
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
}
