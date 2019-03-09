package matcher;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/8.
 * 模式层匹配器接口
 */
public interface SchemaMatcher {

    /**
     * 匹配两个知识库中的类
     * @param ks
     * @param kt
     * @return
     */
    Map<OntClass,Map<OntClass,Double>> classAlign(OntModel ks,OntModel kt);

    /**
     * 匹配两个知识库中的数据类型属性
     * @param ks
     * @param kt
     * @return
     */
    Map<DatatypeProperty,Map<DatatypeProperty,Double>> dpAlign(OntModel ks,OntModel kt);

    /**
     * 匹配两个知识库中的对象属性
     * @param ks
     * @param kt
     * @return
     */
    Map<ObjectProperty,Map<ObjectProperty,Double>> opAlign(OntModel ks,OntModel kt);


}
