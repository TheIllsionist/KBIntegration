package similarity;

import parser.Parser;
import org.apache.jena.ontology.OntResource;
import org.simmetrics.StringMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;

/**
 * Created by The Illsionist on 2019/3/9.
 * 名称相似度计算器,有加权相似度计算模式和投票表决模式
 * 1.加权模式:计算多种字符串相似度,最后加权求和作为名称相似度
 * 2.投票表决模式：给定多种字符串相似度值和阈值,最后满足投票条件的认为匹配
 */
@Component
public class NameSimilarity implements Similarity{

    /**
     * 1.在加权组合模式下,该配置是相似度以及权重配置,要求各种相似度的权重加和必须为1
     * 2.在投票表决模式下,该配置是每种相似度的提取阈值,各种相似度的阈值加和没有阈值要求
     */
    private Map<StringMetric,Double> metricConf = null;  //使用哪些相似度计算方法,每种方法权重是多少
    private int votes = 0; //投票表决模式下的票数

    @Autowired
    private Parser parser;

    /**
     * 1.加权模式下是设置相似度及权重配置
     * 2.投票模式下是设置相似度及阈值
     * @param metricConf
     */
    public void setMetricConf(Map<StringMetric,Double> metricConf){
        this.metricConf = metricConf;
    }


    /**
     * 计算两个资源名称相似度的值,若资源有多个名称,取这多个名称中的相似度最大值
     * @param res1
     * @param res2
     * @return
     */
    @Override
    public double similarityOf(OntResource res1, OntResource res2) throws Exception{
        if(metricConf == null || metricConf.size() == 0){
            throw new Exception("ERROR : 没有为实体名称相似度计算设置度量方式和权重配置 ");
        }
        List<String> names1 = parser.labelsOf(res1);
        List<String> names2 = parser.labelsOf(res2);
        double maxSim = 0.0;
        for(String name1 : names1){
            for(String name2 : names2){
                double tmpSim = simOfName(name1,name2);
                if(maxSim < tmpSim){
                    maxSim = tmpSim;
                }
            }
        }
        return maxSim;
    }



    /**
     * 计算两个名称字符串之间的相似度
     * @param name1
     * @param name2
     * @return
     */
    protected double simOfName(String name1,String name2){
        double res = 0.0;
        for(Map.Entry<StringMetric,Double> entry : metricConf.entrySet()){
            res += entry.getKey().compare(name1,name2) * entry.getValue();
        }
        return res;
    }

    /**
     * 利用内部类实现投票表决
     */
    static final class VoteNameSimilarity extends NameSimilarity{

    }

}
