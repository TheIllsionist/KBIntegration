package similarity;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import parser.Parser;
import specification.FormatVal;
import specification.ValFormatSpec;
import utils.PropMapUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/15.
 *
 * 基于区分性属性信息计算两个实例间的相似度
 * 功能：
 *  1.可计算多种属性值之间的相似度
 *  2.可生成属性集之间的属性映射
 *  3.可基于*投票表决法*组合各属性相似度,票数可以智能选择,也支持手工指定
 */
@Component
public class PropInfoSimilarity implements Similarity {

    @Value("coverThreshold")
    private double coverThreshold;  //属性取值集合重叠程度阈值

    @Value("numThreshold")
    private double numThreshold;    //带单位数值相似度阈值

    @Value("textThreshold")
    private double textThreshold;   //短文本相似度阈值

    @Autowired
    private Parser parser;  //知识库解析器

    @Autowired
    private ValFormatSpec formatSpec;  //DP取值格式规范

    @Autowired
    private ValSimilarity valSimilarity;  //区分性属性值相似度计算工具

    @Autowired
    private PropMapUtil propMapUtil;   //属性映射的工具

    /**
     * 计算实例间相似度
     * 采用投票表决方式
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
        double all = propMap.size();  //总票数
        double votes = 0;             //赞成票数
        for(Map.Entry<DatatypeProperty,DatatypeProperty> propPair : propMap.entrySet()){  //逐对比较
            FormatVal val1 = isDeMap.get(propPair.getKey());
            FormatVal val2 = isDeMap.get(propPair.getValue());
            //计算两个值之间的相似度
            double sim = valSimilarity.similarityOf(val1,val2);
            //利用阈值将带单位值相似度和短文本相似度值转化为投票
            if(val1.isNum()){
                sim = sim >= numThreshold ? 1.0 : 0.0;
            }else if(val1.isLetterStr() || val1.isText()){
                sim = sim >= textThreshold ? 1.0 : 0.0;
            }
            votes += sim;
        }
        return votes/all;  //返回投票率
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
    private Map<DatatypeProperty,DatatypeProperty> propMapping(Map<DatatypeProperty,FormatVal> isDeMap,Map<DatatypeProperty,FormatVal> itDeMap) throws Exception{
        return propMapUtil.mapping(isDeMap,itDeMap);
    }

}
