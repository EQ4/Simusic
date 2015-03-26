/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extractors.feature;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import jsymbolic.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Martin
 */
public class FeatureExtractor {

    String midiPath;
    String featurePath;

    public FeatureExtractor(String midiPath, String featurePath) {
        this.midiPath = midiPath;
        this.featurePath = featurePath;


        File folder = new File(midiPath);
        File[] files = folder.listFiles();



        for (File file : files) {

            if (!file.isFile()) {
                continue;
            }

            try {
                //Extract features to XML
                CommandLine.extractFeatures(
                        file.getCanonicalPath(),
                        featurePath + file.getName() + ".xml",
                        featurePath + file.getName() + "_def.xml", false);

                //Store features from XML
                Document dom = parse(new URL(featurePath + file.getName() + ".xml"));

            } catch (Exception e) {
                System.out.println("Feature Extractor: " + e.getMessage());
            }
        }
    }

    private final Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }
}
