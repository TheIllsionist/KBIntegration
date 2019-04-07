package matcher;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/8.
 * 实例匹配器接口
 * 给定两个知识库ks和t,输出两个知识库之间的实例匹配结果集
 */
public interface InstanceMatcher {

    /**
     * 匹配两个知识库中的实例
     * @param ks
     * @param kt
     * @return
     */
    Map<Individual,Map<Individual,Double>> insAlign(OntModel ks, OntModel kt);

}
