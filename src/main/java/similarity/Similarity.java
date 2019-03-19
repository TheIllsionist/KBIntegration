package similarity;

import org.apache.jena.ontology.OntResource;

/**
 * Created by The Illsionist on 2019/3/9.
 */
public interface Similarity {

    /**
     * 计算两个本体资源之间的相似度
     * 1.加权组合模式下,返回的是相似度值
     * 2.投票表决模式下,返回0表示不匹配,1表示匹配
     * 有些相似度计算方法只有一种模式
     * @param res1
     * @param res2
     * @return
     */
    double similarityOf(OntResource res1,OntResource res2) throws Exception;

}
