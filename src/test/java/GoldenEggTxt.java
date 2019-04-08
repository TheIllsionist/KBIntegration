import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import parser.FileParser;
import parser.Parser;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/4/8.
 */
public class GoldenEggTxt {
    public static void main(String args[]){
        OntModel ks = null;
        String outPath = "G:\\ExperimentSpace\\HkmjGoldenEggs.txt";
        Parser parser = new FileParser();
        PrintWriter writer = null;
        try{
            ks = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            InputStream ksIn = FileManager.get().open("G:/ExperimentSpace/wkjb.owl");
            ks.read(ksIn,null);
            writer = new PrintWriter(new FileWriter(new File(outPath)));
            OntClass hkmj = ks.getOntClass("http://kse.seu.edu.cn/wkjb#航空母舰");
            Map<String,Individual> inses = parser.instancesOf(hkmj);  //航空母舰类的所有实例
            for(Map.Entry<String,Individual> entry : inses.entrySet()){
                writer.println(entry.getKey() + "          http://kse.seu.edu.cn/wgbq#");
            }
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
