package extractor;

import org.apache.jena.ontology.*;

import java.util.List;

/**
 * Created by The Illsionist on 2019/3/8.
 */
public interface Extractor {

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


    //TODO:返回属性的定义域和属性的值域的方法有需要再定义


}
