package similarity;

import org.apache.jena.ontology.OntResource;

/**
 * Created by The Illsionist on 2019/3/15.
 *
 * 基于区分性属性计算两个实例之间的相似度
 * 功能：
 *  1.可计算多种属性值之间的相似度
 *  2.可生成属性集之间的属性映射
 *  3.可基于*投票表决法*组合各属性相似度,票数可以智能选择,也支持手工指定
 */
public class PropValSimilarity implements Similarity{

    @Override
    public double similarityOf(OntResource res1, OntResource res2) throws Exception {
        return 0;
    }

}
