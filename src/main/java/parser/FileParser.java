package parser;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Created by The Illsionist on 2019/3/8.
 * 此类中的方法都是无状态方法
 */
public class FileParser implements Parser {

    /**
     * 返回一个实体的所有可读名称
     * @param resource
     * @return
     */
    @Override
    public List<String> labelsOf(OntResource resource) {
        Iterator<RDFNode> lbNodes = resource.listLabels(null);  //不加任何语言标记限制
        List<String> labels = new ArrayList<>();
        while(lbNodes.hasNext()){
            labels.add(lbNodes.next().toString());
        }
        return labels;
    }

    /**
     * 返回一个实体的所有描述文本
     * @param resource
     * @return
     */
    @Override
    public List<String> commentsOf(OntResource resource) {
        Iterator<RDFNode> cmNodes = resource.listComments(null);  //不加任何语言标记限制
        List<String> comments = new ArrayList<>();
        while(cmNodes.hasNext()){
            comments.add(cmNodes.next().toString());
        }
        return comments;
    }

    /**
     * 返回一个类的所有直接父类
     * @param ontClass
     * @return
     */
    @Override
    public List<OntClass> supClassesOf(OntClass ontClass) {
        Iterator<OntClass> iter = ontClass.listSuperClasses(true); //cls的直接父类
        List<OntClass> supClasses = new ArrayList<>();
        while(iter.hasNext()){
            supClasses.add(iter.next());
        }
        return supClasses;
    }

    /**
     * 返回一个类的所有直接子类
     * @param ontClass
     * @return
     */
    @Override
    public List<OntClass> subClassesOf(OntClass ontClass) {
        Iterator<OntClass> iter = ontClass.listSubClasses(true);  //cls的直接子类
        List<OntClass> subClasses = new ArrayList<>();
        while(iter.hasNext()){
            subClasses.add(iter.next());
        }
        return subClasses;
    }

    /**
     * 返回一个属性的所有直接父属性
     * @param ontProperty
     * @return
     */
    @Override
    public List<OntProperty> supPropsOf(OntProperty ontProperty) {
        ExtendedIterator<? extends OntProperty> iter = ontProperty.listSuperProperties(true);  //直接父属性
        List<OntProperty> supProps = new ArrayList<>();
        while(iter.hasNext()){
            supProps.add(iter.next());
        }
        return supProps;
    }

    /**
     * 返回一个属性的所有直接子属性
     * @param ontProperty
     * @return
     */
    @Override
    public List<OntProperty> subPropsOf(OntProperty ontProperty) {
        ExtendedIterator<? extends OntProperty> iter = ontProperty.listSubProperties(true);  //直接子属性
        List<OntProperty> subProps = new ArrayList<>();
        while(iter.hasNext()){
            subProps.add(iter.next());
        }
        return subProps;
    }

    /**
     * 返回一个类所拥有的所有直接实例
     * @param ontClass
     * @return
     */
    @Override
    public Map<String,Individual> instancesOf(OntClass ontClass) {
        Map<String,Individual> inses = new HashMap<>();
        Iterator<? extends OntResource> iter =  ontClass.listInstances(true);  //cls的直接实例
        while(iter.hasNext()){
            Individual ins = (Individual) iter.next();
            inses.put(ins.getURI(),ins);
        }
        return inses;
    }

    /**
     * 返回一个知识库中的所有实例
     * @param ontModel
     * @return
     */
    @Override
    public Map<String,Individual> instancesOf(OntModel ontModel) {
        Map<String,Individual> results = new HashMap<>();
        ExtendedIterator<Individual> inses = ontModel.listIndividuals();
        while(inses.hasNext()){
            Individual ins = inses.next();
            results.put(ins.getURI(),ins);
        }
        return results;
    }

    /**
     * 返回一个类所拥有的属性
     * @param ontClass
     * @param ontModel
     * @param percent &nbsp 此类中有多少比例的实例有这个属性才可以说这个类拥有这个属性
     * @return
     */
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

    /**
     * 返回一个类所拥有的数据类型属性
     * @param ontClass
     * @param ontModel
     * @param percent
     * @return
     */
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

    /**
     * 返回一个属性的定义域所包含的类集
     * @param prop
     * @param ontModel
     * @param percent
     * @return
     */
    @Override
    public List<OntClass> domainOfProp(OntProperty prop, OntModel ontModel, double percent) {
        List<OntClass> clses = new ArrayList<>();
        ExtendedIterator<OntClass> clsIter = ontModel.listClasses();
        while(clsIter.hasNext()){  //迭代知识库中的所有类
            OntClass tCls = clsIter.next();
            List<OntProperty> props = propsOfCls(tCls,ontModel,percent); //计算该类拥有的属性
            if(props.contains(prop)){ //如果该类拥有这个属性,则这个属性的定义域包括该类
                clses.add(tCls);
            }
        }
        return clses;
    }

    /**
     * 返回一个属性的值域所包含的类集
     * @param op
     * @param ontModel
     * @param percent
     * @return
     */
    @Override
    public List<OntClass> rangeOfOp(ObjectProperty op, OntModel ontModel, double percent) {
        Set<OntClass> clses = new HashSet<>();
        SimpleSelector selector = new SimpleSelector(null,op, (Object)null){
            @Override
            public boolean test(Statement s){
                String sUri = s.getPredicate().getURI();
                return this.predicate.getURI().equals(sUri) ? true :false;
            }
            @Override
            public boolean selects(Statement s){
                String sUri = s.getPredicate().getURI();
                return this.predicate.getURI().equals(sUri) ? true : false;
            }
        };
        StmtIterator iterator = ontModel.listStatements(selector);
        while(iterator.hasNext()){
            Statement statement = iterator.nextStatement();
            RDFNode node = statement.getObject();
            if(node.isResource() && node.asResource().hasProperty(RDF.type,OWL2.NamedIndividual)){
                Individual ins = ontModel.getIndividual(node.asResource().getURI());
                Iterator<OntClass> clsIter = ins.listOntClasses(true);
                while(clsIter.hasNext()){
                    clses.add(clsIter.next());
                }
            }
        }
        List<OntClass> res = new ArrayList<>();
        Iterator<OntClass> clsIter = clses.iterator();
        while(clsIter.hasNext()){
            res.add(clsIter.next());
        }
        return res;
    }

    /**
     * 返回一个实例所拥有的所有数据类型属性及其值
     * @param individual
     * @return
     */
    @Override
    public Map<DatatypeProperty, String> dpValsOf(Individual individual) {
        Map<DatatypeProperty,String> dpVals = new HashMap<>();
        StmtIterator sIter = individual.listProperties();  //迭代实例的所有属性
        while(sIter.hasNext()){
            Statement stmt = sIter.nextStatement();
            Property tp = stmt.getPredicate();
            if(tp.hasProperty(RDF.type, OWL.DatatypeProperty)){  //当前属性是数据类型属性
                RDFNode obj = stmt.getObject();  //TODO:注意,这里默认一个实例的一个数据类型属性只有一个取值
                DatatypeProperty dp = individual.getOntModel().getDatatypeProperty(tp.getURI());
                String val = obj.asLiteral().toString();
                val = val.replaceAll("\\s+|,|，","").replaceAll("～","~").replaceAll("浬|海浬|海哩","海里");
                dpVals.put(dp,val);
            }
        }
        return dpVals;
    }

}
