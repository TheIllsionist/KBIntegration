package similarity;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntResource;
import parser.Parser;
import specification.FormatVal;
import specification.ValFormatSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private Parser parser;

    @Autowired
    private ValFormatSpec formatSpec;



    @Override
    public double similarityOf(OntResource res1, OntResource res2) throws Exception {
        Individual ins1 = res1.asIndividual();
        Individual ins2 = res2.asIndividual();
        Map<DatatypeProperty,String> dpMap1 = parser.dpValsOf(ins1);
        Map<DatatypeProperty,String> dpMap2 = parser.dpValsOf(ins2);
        Map<DatatypeProperty,FormatVal> dmMap1 = new HashMap<>();
        Map<DatatypeProperty,FormatVal> dmMap2 = new HashMap<>();

    }

    private Map<DatatypeProperty,FormatVal> extractDm(Map<DatatypeProperty,String> dpMap){

    }

    private Map<DatatypeProperty,List<DatatypeProperty>> propMapping(){

    }

    private double simOfNum(Double sNum,Double tNum){

    }

    private double

}
