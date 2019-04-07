package similarity;

import org.apache.jena.ontology.OntResource;
import org.springframework.beans.factory.annotation.Qualifier;
import parser.Parser;
import org.simmetrics.StringMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 *
 * 实体名称相似度计算器,支持 加权模式 和 投票模式
 * 1.加权模式：计算多种字符串相似度,最后加权求和作为名称相似度
 * 2.投票模式：给定多种字符串相似度和阈值,最后满足投票条件的认为匹配
 */
public class NameSimilarity implements Similarity {

    private boolean isVote = false;  //true表示采用投票表决模式

    /**
     * 1.加权模式：配置相似度度量及其权重,要求各种权重加和必须为1
     * 2.投票表决模式：配置相似度度量和提取阈值,各种阈值加和没有要求
     */
    private Map<StringMetric,Double> metricConf = null;
    private double voteRateThreshold = -1.0;  //投票率阈值

    @Autowired
    private Parser parser; //知识库解析器


    /**
     * 构造函数
     * @param isVote &nbsp 是否采用投票表决模式
     */
    public NameSimilarity(boolean isVote){
        this.isVote = isVote;
    }

    /**
     * 1.加权模式设置相似度及权重配置
     * 2.投票模式设置相似度及阈值配置
     * @param metricConf
     */
    public void setMetricConf(Map<StringMetric,Double> metricConf){
        this.metricConf = metricConf;
    }

    /**
     * 设置投票率阈值
     * @param voteRateThreshold
     */
    public void setVoteRateThreshold(double voteRateThreshold){
        this.voteRateThreshold = voteRateThreshold;
    }

    /**
     * 根据名称相似度判断两个实体是否匹配
     * 只有在投票模式下才能调用该方法
     * @param i
     * @param j
     * @return
     * @throws Exception
     */
    public boolean isMatch(OntResource i,OntResource j) throws Exception{
        if(!isVote){
            throw new Exception("ERROR : 不合法的方法调用,在加权模式下不能调用该方法!");
        }
        if(voteRateThreshold < 0){
            throw new Exception("ERROR : 没有为实体名称相似度比较设置投票率阈值");
        }
        return similarityOf(i,j) >= this.voteRateThreshold ? true : false;
    }

    /**
     * 1.相似度组合模式:计算实体相似度的值,若实体有多个名称,取相似度最大值
     * @param i
     * @param j
     * @return
     */
    @Override
    public double similarityOf(OntResource i, OntResource j) throws Exception{
        if(metricConf == null || metricConf.size() == 0){
            throw new Exception("ERROR : 没有为实体名称相似度计算设置度量方式和阈值或权重配置 ");
        }
        List<String> names1 = parser.labelsOf(i);
        List<String> names2 = parser.labelsOf(j);
        double max = 0.0;
        if(isVote){  //采用投票表决方式
            for(String name1 : names1){
                for(String name2 : names2){
                    double tmpVoteRate = voteRateOfName(name1,name2);
                    if(tmpVoteRate > max){
                        max = tmpVoteRate;  //取最大投票率
                    }
                }
            }
        }else{     //采用加权组合方式
            for(String name1 : names1){
                for(String name2 : names2){
                    double tmpSim = simOfName(name1,name2);
                    if(tmpSim > max){
                        max = tmpSim;      //取最大相似度值
                    }
                }
            }
        }
        return max;
    }

    /**
     * 计算两个名称字符串之间的加权相似度
     * @param name1
     * @param name2
     * @return
     */
    private double simOfName(String name1,String name2){
        double res = 0.0;
        for(Map.Entry<StringMetric,Double> entry : metricConf.entrySet()){
            res += entry.getKey().compare(name1,name2) * entry.getValue();
        }
        return res;
    }

    /**
     * 计算两个名称字符串之间的支持投票率
     * @param name1
     * @param name2
     * @return
     */
    private double voteRateOfName(String name1,String name2){
        double all = metricConf.size(); //总票数
        int votes = 0;  //支持票数
        for(Map.Entry<StringMetric,Double> entry : metricConf.entrySet()){
            double tmpSim = entry.getKey().compare(name1,name2);
            if(tmpSim >= entry.getValue())  //名称相似度大于相似度阈值
                votes++;  //票数+1
        }
        return votes/all;
    }



}
