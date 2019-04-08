package evaluation;

import org.apache.jena.ontology.Individual;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/4/7.
 * 基于区分性属性信息进行实例匹配的评估器
 * 该评估器计算 精确率和召回率
 */
@Component
public class Evaluator {

    @Value("goldenEggPath")
    private String goldenEggs;  //黄金数据集文件路径

    private Map<String,Individual> ises = null;  //源知识库实例集
    private Map<String,Individual> ites = null;  //目标知识库实例集

    private Map<Individual,Individual> pairG = null;  //黄金数据集
    private Map<Individual,Individual> pairF = null;  //负例数据集

    private double precision;   //精确率
    private double recall;      //召回率
    private double F1;          //F1值


    public void setIses(Map<String,Individual> ises){
        this.ises = ises;
    }

    public void setItes(Map<String,Individual> ites){
        this.ites = ites;
    }

    /**
     * 评估器初始化方法
     * 读取黄金数据集,同时初始化负例数据集
     */
    public void init() throws Exception{
        if(ises == null || ites == null){
            new Exception("评估器没有源知识库实例集或者没有目标知识库实例集");
        }
        //初始化黄金数据集
        pairG = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(goldenEggs)));
        String line = null;
        line = reader.readLine();
        while(line != null){
            String[] tmp = line.split("\\s+");
            Individual i = ises.get(tmp[0]);
            Individual j = ites.get(tmp[1]);
            pairG.put(i,j);
            line = reader.readLine();
        }
        //初始化负例数据集
        pairF = new HashMap<>();
        //pairF = pairA - pairG
        for(Individual i : ises.values()){
            for(Individual j : ites.values()){
                if(!pairG.containsKey(i) || pairG.get(i) != j){
                    pairF.put(i,j);
                }
            }
        }
    }

    /**
     * 评估方法,根据输入的匹配结果集评估,记录精确率,召回率和F值
     * @param pairM
     */
    public void evaluate(Map<Individual,Individual> pairM){
        double tp = 0.0,fn = 0.0,fp = 0.0,tn = 0.0;
        for(Map.Entry<Individual,Individual> entry : pairG.entrySet()){
            if(!pairM.containsKey(entry.getKey()) || pairM.get(entry.getKey()) != entry.getValue())
                fn++;
            else
                tp++;
        }
        for(Map.Entry<Individual,Individual> entry : pairF.entrySet()){
            if(!pairM.containsKey(entry.getKey()) || pairM.get(entry.getKey()) != entry.getValue())
                tn++;
            else
                fp++;
        }
        precision = tp / (tp + fp);
        recall = tp / (tp + fn);
        F1 = (2 * tp) / (2 * tp + fp + fn);
    }


    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1() {
        return F1;
    }

}
