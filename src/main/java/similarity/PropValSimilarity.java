package similarity;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntResource;
import parser.Parser;
import specification.FormatVal;
import specification.ValFormatSpec;
import utils.PropMapUtil;
import java.util.HashMap;
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

    @Autowired
    private ValSimilarity valSimilarity;

    @Autowired
    private PropMapUtil propMapUtil;



    @Override
    public double similarityOf(OntResource es, OntResource et) throws Exception {
        Individual is = es.asIndividual();
        Individual it = et.asIndividual();
        Map<DatatypeProperty,String> isDpMap = parser.dpValsOf(is); //is的DP属性集
        Map<DatatypeProperty,String> itDpMap = parser.dpValsOf(it); //it的DP属性集
        Map<DatatypeProperty,FormatVal> isDeMap = new HashMap<>();  //is的DE提取属性集
        Map<DatatypeProperty,FormatVal> itDeMap = new HashMap<>();  //it的DE提取属性集


    }

    /**
     * 根据知识库知识表示规范中的属性值取值格式从DP属性值中提取DE属性集
     * 如果出现没有任何匹配格式的属性值,打印到控制台上
     * @param dpMap
     * @return
     */
    private Map<DatatypeProperty,FormatVal> extractDeMap(Individual ins, Map<DatatypeProperty,String> dpMap){
        Map<DatatypeProperty,FormatVal> deMap = new HashMap<>();
        for(Map.Entry<DatatypeProperty,String> entry : dpMap.entrySet()){
            FormatVal tVal = null;
            try{
                tVal = formatSpec.formatVal(entry.getValue());
                deMap.put(entry.getKey(),tVal);
            }catch (Exception e){
                System.out.println("   ERROR : 没有对应于实例 " + ins.getURI() + " 的属性 "
                        + entry.getKey().getURI() + " 的属性值 " + entry.getValue() + " 的匹配格式.");
            }
        }
        return deMap;
    }

    private Map<DatatypeProperty,DatatypeProperty> propMapping(Map<DatatypeProperty,FormatVal> isDeMap,Map<DatatypeProperty,FormatVal> itDeMap){
        return propMapUtil.mapping(isDeMap,itDeMap);
    }

}
