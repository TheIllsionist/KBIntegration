package similarity;

import extractor.Extractor;
import org.apache.jena.ontology.OntResource;
import org.simmetrics.StringMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/9.
 * 名称相似度计算器
 */
@Component
public class NameSimilarity implements Similarity{

    private Map<StringMetric,Double> metricConf = null;  //使用哪些相似度计算方法,每种方法权重是多少

    @Autowired
    private Extractor extractor;


    /**
     * 设置名称相似度计算配置,用哪种度量以及这种度量的占比
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
        List<String> names1 = extractor.labelsOf(res1);
        List<String> names2 = extractor.labelsOf(res2);
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
    private double simOfName(String name1,String name2){
        double res = 0.0;
        for(Map.Entry<StringMetric,Double> entry : metricConf.entrySet()){
            res += entry.getKey().compare(name1,name2) * entry.getValue();
        }
        return res;
    }


}
