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
    private Parser parser;  //知识库解析器

    @Autowired
    private ValFormatSpec formatSpec;  //DP取值格式规范

    @Autowired
    private ValSimilarity valSimilarity;  //区分性属性值相似度计算

    @Autowired
    private PropMapUtil propMapUtil;   //属性映射的工具

    /**
     * 计算实例间相似度
     * @param es
     * @param et
     * @return
     * @throws Exception
     */
    @Override
    public double similarityOf(OntResource es, OntResource et) throws Exception {
        Individual is = es.asIndividual();
        Individual it = et.asIndividual();
        Map<DatatypeProperty,String> isDpMap = parser.dpValsOf(is); //is的DP属性集
        Map<DatatypeProperty,String> itDpMap = parser.dpValsOf(it); //it的DP属性集
        Map<DatatypeProperty,FormatVal> isDeMap = extractDeMap(is,isDpMap);  //is的DE提取属性集
        Map<DatatypeProperty,FormatVal> itDeMap = extractDeMap(it,itDpMap);  //it的DE提取属性集
        Map<DatatypeProperty,DatatypeProperty> propMap = propMapping(isDeMap,itDeMap);  //属性映射结果
        for(Map.Entry<DatatypeProperty,DatatypeProperty> propPair : propMap.entrySet()){  //逐对比较
            FormatVal val1 = isDeMap.get(propPair.getKey());
            FormatVal val2 = isDeMap.get(propPair.getValue());
            //计算两个值之间的相似度

            //各相似度值加权或根据阈值转化为投票

        }
        //加权方法返回总体相似度
        //投票方法返回0或1
        return 0.0;
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

    /**
     * 进行属性映射
     * @param isDeMap
     * @param itDeMap
     * @return
     */
    private Map<DatatypeProperty,DatatypeProperty> propMapping(Map<DatatypeProperty,FormatVal> isDeMap,Map<DatatypeProperty,FormatVal> itDeMap){
        return propMapUtil.mapping(isDeMap,itDeMap);
    }

}
