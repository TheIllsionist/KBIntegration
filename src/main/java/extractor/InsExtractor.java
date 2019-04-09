package extractor;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import parser.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/4/9.
 * 实例提取器,根据输入的提取条件提取实例集进行匹配,目前只支持根据类别提取实例集
 */
public class InsExtractor {

    private Parser parser;
    private List<String> ksClasses;
    private List<String> ktClasses;

    public void setParser(Parser fileParser){
        this.parser = fileParser;
    }

    public void setKsClasses(List<String> ksClasses){
        this.ksClasses = ksClasses;
    }

    public void setKtClasses(List<String> ktClasses){
        this.ktClasses = ktClasses;
    }

    /**
     * 根据源知识库类名集合提取源实例集
     * @param ks
     * @return
     */
    public Map<String,Individual> extractKs(OntModel ks){
        return extract(ks,ksClasses);
    }

    /**
     * 根据目标知识库类名集合提取目标实例集
     * @param kt
     * @return
     */
    public Map<String,Individual> extractKt(OntModel kt){
        return extract(kt,ktClasses);
    }

    /**
     * 给定知识库和类名集合,根据类名集合从该知识库中提取实例集
     * @param model
     * @param clses
     * @return
     */
    private Map<String,Individual> extract(OntModel model,List<String> clses){
        Map<String,Individual> res = new HashMap<>();
        for(String s : clses){
            OntClass cls = model.getOntClass(s);
            Map<String,Individual> inses = parser.instancesOf(cls);
            res.putAll(inses);
        }
        return res;
    }


}
