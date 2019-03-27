package utils;

import org.apache.jena.ontology.DatatypeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import similarity.ValSimilarity;
import specification.FormatVal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by The Illsionist on 2019/3/22.
 * 属性映射工具,为了找到对匹配实例有用的属性对,去捕获描述了一对实例之间某一相同方面特征的属性对
 * 使用信息：
 *      a.属性名
 *      b.取值格式
 *      c.属性取值的重叠程度
 *      d.属性值相似度
 */
public class PropMapUtil {

    private double overlapThreshold;  //属性取值重叠程度阈值,高于此阈值则认为两个属性是映射属性

    @Autowired
    private ValSimilarity valSimilarity;

    /**
     * 给定两个属性集,寻找这两个属性集之间的属性映射
     * @param isDeMap &nbsp 源库实例的提取属性集
     * @param itDeMap &nbsp 目标库实例的提取属性集
     * @return
     */
    public Map<DatatypeProperty,DatatypeProperty> mapping(Map<DatatypeProperty, FormatVal> isDeMap, Map<DatatypeProperty, FormatVal> itDeMap){
        Map<DatatypeProperty,DatatypeProperty> mapping = new HashMap<>();
        if(isDeMap == null || isDeMap.size() == 0 || itDeMap == null || itDeMap.size() == 0)
            return mapping;
        for(Map.Entry<DatatypeProperty,FormatVal> isEntry : isDeMap.entrySet()){
//            Map<Double,DatatypeProperty> overlapSimMap = new TreeMap<>();  //TODO:先不做这个(当前属性跟其他具有相同格式属性的属性取值的重叠程度)
            TreeMap<Double,DatatypeProperty> valSimMap = new TreeMap<>();      //当前属性跟其他具有相同格式属性的属性值相似度
            for(Map.Entry<DatatypeProperty,FormatVal> itEntry : itDeMap.entrySet()){
                if(isEntry.getKey().getLabel(null).equals(itEntry.getKey().getLabel(null))){  //两属性名称相同,直接作为映射属性
                    mapping.put(isEntry.getKey(),itEntry.getKey());
                    continue;  //跳过itDeMap的后续所有属性
                }else if(isEntry.getValue().sameFormatWith(itEntry.getValue())){    //名称不同但取值格式相同
                    double valSim = valSimilarity.similarityOf(isEntry.getValue(),itEntry.getValue());
                    valSimMap.put(valSim,itEntry.getKey());
                }else{       //名称不同,并且取值格式也不同
                    continue;  //取值格式不同的情况下,根本不能做相似度计算,跳过
                }
            }
            if(valSimMap.size() != 0){
                mapping.put(isEntry.getKey(),valSimMap.get(valSimMap.lastKey())); //TreeMap默认升序排序
            }
        }
        return mapping;
    }


}
