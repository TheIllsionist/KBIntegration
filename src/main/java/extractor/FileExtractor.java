package extractor;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by The Illsionist on 2019/3/8.
 * 此类中的方法都是无状态方法
 */
@Component("extractor")
public class FileExtractor implements Extractor{

    @Override
    public List<String> labelsOf(OntResource resource) {
        Iterator<RDFNode> lbNodes = resource.listLabels(null);  //不加任何语言标记限制
        List<String> labels = new ArrayList<>();
        while(lbNodes.hasNext()){
            labels.add(lbNodes.next().toString());
        }
        return labels;
    }

    @Override
    public List<String> commentsOf(OntResource resource) {
        Iterator<RDFNode> cmNodes = resource.listComments(null);  //不加任何语言标记限制
        List<String> comments = new ArrayList<>();
        while(cmNodes.hasNext()){
            comments.add(cmNodes.next().toString());
        }
        return comments;
    }

    @Override
    public List<OntClass> supClassesOf(OntClass ontClass) {
        Iterator<OntClass> iter = ontClass.listSuperClasses(true); //cls的直接父类
        List<OntClass> supClasses = new ArrayList<>();
        while(iter.hasNext()){
            supClasses.add(iter.next());
        }
        return supClasses;
    }


    @Override
    public List<OntClass> subClassesOf(OntClass ontClass) {
        Iterator<OntClass> iter = ontClass.listSubClasses(true);  //cls的直接子类
        List<OntClass> subClasses = new ArrayList<>();
        while(iter.hasNext()){
            subClasses.add(iter.next());
        }
        return subClasses;
    }


    @Override
    public List<OntProperty> supPropsOf(OntProperty ontProperty) {
        ExtendedIterator<? extends OntProperty> iter = ontProperty.listSuperProperties(true);  //直接父属性
        List<OntProperty> supProps = new ArrayList<>();
        while(iter.hasNext()){
            supProps.add(iter.next());
        }
        return supProps;
    }


    @Override
    public List<OntProperty> subPropsOf(OntProperty ontProperty) {
        ExtendedIterator<? extends OntProperty> iter = ontProperty.listSubProperties(true);  //直接子属性
        List<OntProperty> subProps = new ArrayList<>();
        while(iter.hasNext()){
            subProps.add(iter.next());
        }
        return subProps;
    }


    @Override
    public List<Individual> instancesOf(OntClass ontClass) {
        Iterator<? extends OntResource> iter =  ontClass.listInstances(true);  //cls的直接实例
        List<Individual> individuals = new ArrayList<>();
        while(iter.hasNext()){
            individuals.add(iter.next().asIndividual());
        }
        return individuals;
    }


    @Override
    public List<OntProperty> propsOfCls(OntClass ontClass, OntModel ontModel, double percent) {
        int insCount = 0;  //实例计数
        Map<OntProperty,Integer> propCounts = new HashMap<>();  //属性计数
        Iterator<? extends OntResource> individuals = ontClass.listInstances(true);
        while(individuals.hasNext()){
            Individual ins = individuals.next().asIndividual();
            insCount++;  //实例数 +1
            StmtIterator iterator = ins.listProperties(); //迭代属性取值
            while(iterator.hasNext()){
                Statement statement = iterator.nextStatement();   //TODO:这里适用条件是每个实例的每个属性都只出现了一次,后续需要做调试检查
                OntProperty ontP = ontModel.getOntProperty(statement.getPredicate().getURI());
                if(ontP != null){
                    propCounts.put(ontP,propCounts.get(ontP) == null ? 1 : propCounts.get(ontP) + 1);
                }
            }
        }
        List<OntProperty> reses = new ArrayList<>();
        for(Map.Entry<OntProperty,Integer> entry : propCounts.entrySet()){
            if(entry.getValue()/(double)insCount >= percent){  //有占比大于percent的实例拥有这个属性,则说这个类包含这个属性
                reses.add(entry.getKey());
            }
        }
        return reses;
    }



    @Override
    public List<DatatypeProperty> dpsOfCls(OntClass ontClass, OntModel ontModel, double percent) {
        List<OntProperty> props = propsOfCls(ontClass,ontModel,percent);
        List<DatatypeProperty> dps = new ArrayList<>();
        for(OntProperty prop : props){
            if(prop.isDatatypeProperty()){
                dps.add(prop.asDatatypeProperty());
            }
        }
        return dps;
    }

}
