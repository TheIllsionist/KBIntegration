package matcher.impl;

import matcher.InstanceMatcher;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import parser.Parser;
import similarity.Similarity;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by The Illsionist on 2019/4/7.
 * 基于区分性属性信息的实例匹配器
 */
@Component
public class PropInfoMatcher implements InstanceMatcher{

    @Autowired
    Parser parser;

//    @Autowired
//    @Qualifier("nameSimilarity")
//    Similarity nameSimilarity;

    @Autowired
    @Qualifier("propInfoSimilarity")
    Similarity propInfoSimilarity;

    @Value("${voteThreshold}")
    double voteThreshold;  //投票率阈值

    /**
     * 匹配源知识库ks和目标知识库kt中的实例
     * @param ks
     * @param kt
     * @return
     * @throws Exception
     */
    @Override
    public Map<Individual,Individual> insAlign(Map<String,Individual> ises, Map<String,Individual> ites) throws Exception{
        Map<Individual,TreeMap<Double,Individual>> preRes = new HashMap<>();  //初步匹配结果
        for(Individual i : ises.values()){
            for(Individual j : ites.values()){
                //TODO:先计算名称相似度,只有名称相似度满足一定条件的实例再基于区分性属性信息计算相似度
                double voteRate = propInfoSimilarity.similarityOf(i,j);  //得到此对实例的投票率
                if(voteRate >= voteThreshold){  //投票率通过
                    if(!preRes.containsKey(i)){
                        TreeMap<Double,Individual> tmp = new TreeMap<>();  //默认按照投票率排序
                        tmp.put(voteRate,j);
                        preRes.put(i,tmp);
                    }else{
                        preRes.get(i).put(voteRate,j);
                    }
                }
            }
        }
        Map<Individual,Individual> pairM = new HashMap<>();  //匹配结果
        for(Map.Entry<Individual,TreeMap<Double,Individual>> entry : preRes.entrySet()){
            pairM.put(entry.getKey(),entry.getValue().lastEntry().getValue());  //若有多个结果,选择投票率最大的那个
        }
        return pairM;
    }

}
