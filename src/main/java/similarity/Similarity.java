package similarity;

import org.apache.jena.ontology.OntResource;

/**
 * Created by The Illsionist on 2019/4/6.
 * 实体相似度接口
 */
public interface Similarity {

    /**
     * 计算两个实体之间的相似度
     * 1.加权组合模式下,返回的是相似度值
     * 2.投票表决模式下,返回的是赞成票数占总票数的百分比(TODO:暂时这么处理)
     * @param i
     * @param j
     * @return
     * @throws Exception
     */
    double similarityOf(OntResource i, OntResource j) throws Exception;

}
