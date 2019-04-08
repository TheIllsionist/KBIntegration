package utils;

import org.apache.jena.ontology.DatatypeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.DefaultMessageCodesResolver;
import similarity.ValSimilarity;
import specification.FormatVal;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by The Illsionist on 2019/3/22.
 * 属性映射工具,为了找到对匹配实例有用的属性对,去捕获描述了一对实例之间某一相同方面特征的属性对
 * 使用信息：
 *      a.属性名
 *      b.取值格式
 *      c.属性取值的重叠程度(暂时没用)
 *      d.属性值相似度(在有多个属性值相似度通过的情况下,用属性名相似度高的作为映射)
 */
@Component
public class PropMapUtil {

    @Autowired
    private ValSimilarity valSimilarity;

    /**
     * 给定两个属性集,寻找这两个属性集之间的属性映射
     * @param isDeMap &nbsp 源库实例的提取属性集
     * @param itDeMap &nbsp 目标库实例的提取属性集
     * @return
     */
    public Map<DatatypeProperty,DatatypeProperty> mapping(Map<DatatypeProperty, FormatVal> isDeMap, Map<DatatypeProperty, FormatVal> itDeMap) throws Exception{
        Map<DatatypeProperty,DatatypeProperty> mapping = new HashMap<>();  //映射结果
        if(isDeMap == null || isDeMap.size() == 0 || itDeMap == null || itDeMap.size() == 0)
            return mapping;
        Map.Entry<DatatypeProperty,FormatVal> isEntry = null;
        Map.Entry<DatatypeProperty,FormatVal> itEntry = null;
        Iterator<Map.Entry<DatatypeProperty,FormatVal>> isIter = isDeMap.entrySet().iterator();
        //首先按照名称发现映射属性对
        while(isIter.hasNext()){
            isEntry = isIter.next();
            Iterator<Map.Entry<DatatypeProperty,FormatVal>> itIter = itDeMap.entrySet().iterator();
            while(itIter.hasNext()){
                itEntry = itIter.next();
                if(isEntry.getKey().getLabel(null).equals(itEntry.getKey().getLabel(null))){
                    mapping.put(isEntry.getKey(),itEntry.getKey());
                    isIter.remove();
                    itIter.remove();
                    break;
                }
            }
        }
//        TreeMap<Double,DatatypeProperty> coverMap = new TreeMap();        //TODO:目前没有做重叠程度度量
        //名称不同,按照值格式筛选且计算取值相似度
        TreeMap<Double,DatatypeProperty> valSimMap = new TreeMap<>();     //当前属性跟其他格式相同属性的值相似度
        for(Map.Entry<DatatypeProperty,FormatVal> sEntry : isDeMap.entrySet()){
            for(Map.Entry<DatatypeProperty,FormatVal> tEntry : itDeMap.entrySet()){
                if(sEntry.getValue().sameFormatWith(tEntry.getValue())){
                    double valSim = valSimilarity.similarityOf(sEntry.getValue(),tEntry.getValue());
                    valSimMap.put(valSim,tEntry.getKey());
                }
            }
            if(valSimMap.size() != 0){  //格式相同情况下,选择值相似度最大的那个属性作映射属性
                mapping.put(sEntry.getKey(),valSimMap.get(valSimMap.lastKey()));
                valSimMap.clear(); //清空以由下一个sEntry使用
            }
        }
        return mapping;
    }


//    /**
//     * 给定两个属性集,寻找这两个属性集之间的属性映射
//     * @param isDeMap &nbsp 源库实例的提取属性集
//     * @param itDeMap &nbsp 目标库实例的提取属性集
//     * @return
//     */
//    public Map<DatatypeProperty,DatatypeProperty> mapping(Map<DatatypeProperty, FormatVal> isDeMap, Map<DatatypeProperty, FormatVal> itDeMap) throws Exception{
//        Map<DatatypeProperty,DatatypeProperty> mapping = new HashMap<>();  //映射结果
//        if(isDeMap == null || isDeMap.size() == 0 || itDeMap == null || itDeMap.size() == 0)
//            return mapping;
//        TreeMap<Double,DatatypeProperty> valSimMap = new TreeMap<>();     //当前属性跟其他格式相同属性的值相似度
//        for(Map.Entry<DatatypeProperty,FormatVal> isEntry : isDeMap.entrySet()){
//            for(Map.Entry<DatatypeProperty,FormatVal> itEntry : itDeMap.entrySet()){
//                if(isEntry.getValue().sameFormatWith(itEntry.getValue())){  //取值格式相同,计算值相似度并记录
//                    double valSim = valSimilarity.similarityOf(isEntry.getValue(),itEntry.getValue());
//                    valSimMap.put(valSim,itEntry.getKey());
//                }
//            }
//            mapping.put(isEntry.getKey(),valSimMap.get(valSimMap.lastKey()));  //取格式相同的属性中,取值相似度最大的属性作为映射属性
//        }
//        return mapping;
//    }


}
