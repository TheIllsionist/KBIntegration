package parser;

import org.apache.jena.ontology.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by The Illsionist on 2019/3/8.
 */
public interface Parser {

    /**
     * 返回一个本体资源的所有可读名称
     * @param resource
     * @return
     */
    List<String> labelsOf(OntResource resource);


    /**
     * 返回一个本体资源的所有解释说明
     * @param resource
     * @return
     */
    List<String> commentsOf(OntResource resource);


    /**
     * 返回一个本体类的所有直接父类(很可能只有一个)
     * @param ontClass
     * @return
     */
    List<OntClass> supClassesOf(OntClass ontClass);


    /**
     * 返回一个本体类的所有直接子类(可能有多个)
     * @param ontClass
     * @return
     */
    List<OntClass> subClassesOf(OntClass ontClass);


    /**
     * 返回一个本体属性的所有直接父属性
     * @param ontProperty
     * @return
     */
    List<OntProperty> supPropsOf(OntProperty ontProperty);


    /**
     * 返回一个本体属性的所有直接子属性
     * @param ontProperty
     * @return
     */
    List<OntProperty> subPropsOf(OntProperty ontProperty);


    /**
     * 返回一个本体类的所有实例(直接实例)
     * @param ontClass
     * @return
     */
    List<Individual> instancesOf(OntClass ontClass);

    /**
     * 返回一个知识库中的所有实例
     * @param ontModel
     * @return
     */
    Map<String,Individual> instancesOf(OntModel ontModel);


    /**
     * 返回一个本体类所拥有的属性
     * @param ontClass
     * @param ontModel
     * @param percent &nbsp 此类中有多少比例的实例有这个属性才可以说这个类拥有这个属性
     * @return
     */
    List<OntProperty> propsOfCls(OntClass ontClass, OntModel ontModel,double percent);


    /**
     * 返回一个本体类所拥有的DP属性
     * @param ontClass
     * @param ontModel
     * @param percent
     * @return
     */
    List<DatatypeProperty> dpsOfCls(OntClass ontClass, OntModel ontModel, double percent);


    /**
     * 定义属性的定义域为:拥有该属性的类的集合
     * @param prop
     * @param ontModel
     * @param percent
     * @return
     */
    List<OntClass> domainOfProp(OntProperty prop, OntModel ontModel, double percent);


    /**
     * 定义对象属性(数据类型属性值域为字面量)的值域为:该OP的值实例所属的类集合
     * @param op
     * @param ontModel
     * @param percent
     * @return
     */
    List<OntClass> rangeOfOp(ObjectProperty op, OntModel ontModel, double percent);


    /**
     * 返回一个实例所拥有的所有数据类型属性及其值
     * @param individual
     * @return
     */
    Map<DatatypeProperty,String> dpValsOf(Individual individual);

}
