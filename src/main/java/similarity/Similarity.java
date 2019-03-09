package similarity;

import org.apache.jena.ontology.OntResource;

/**
 * Created by The Illsionist on 2019/3/9.
 */
public interface Similarity {

    /**
     * 计算两个本体资源之间的相似度
     * @param res1
     * @param res2
     * @return
     */
    double similarityOf(OntResource res1,OntResource res2) throws Exception;

}
