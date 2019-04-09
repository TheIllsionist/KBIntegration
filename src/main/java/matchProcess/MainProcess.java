package matchProcess;

import evaluation.Evaluator;
import extractor.InsExtractor;
import matcher.InstanceMatcher;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/4/7.
 */
public class MainProcess {

    public static void main(String args[]) throws Exception {
        String ksPath = "G:\\ExperimentSpace\\wkjb.owl";  //源知识库(wkjb)路径
        String ktPath = "G:\\ExperimentSpace\\wgbq.owl";  //目标知识库(wgbq)路径
        OntModel ks = null;
        OntModel kt = null;
        //载入源知识库和目标知识库
        try{
            ks = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            kt = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            InputStream ksIn = FileManager.get().open(ksPath);
            InputStream ktIn = FileManager.get().open(ktPath);
            ks.read(ksIn,null);
            kt.read(ktIn,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml"); //得到IOC容器
//        NameSimilarity nameSim = (NameSimilarity)ioc.getBean("nameSimilarity");//投票表决,投票率阈值0.5
//        //为名称相似度设置两种相似度度量
//        Map<StringMetric,Double> nameSimConf = new HashMap<>();
//        nameSimConf.put(StringMetrics.levenshtein(),0.78);  //编辑距离相似度要求为0.77
//        nameSimConf.put(StringMetrics.jaroWinkler(),0.85);  //jaroWinkler相似度要求0.85
//        nameSim.setMetricConf(nameSimConf);
        InsExtractor extractor = (InsExtractor)ioc.getBean("extractor");  //待匹配实例集提取器
        InstanceMatcher matcher = (InstanceMatcher)ioc.getBean("propInfoMatcher"); //基于区分性属性的匹配器
        Evaluator evaluator = (Evaluator)ioc.getBean("evaluator");     //结果评估器
        //提取实例
        Map<String,Individual> ises = extractor.extractKs(ks);
        Map<String,Individual> ites = extractor.extractKt(kt);
        evaluator.setIses(ises);
        evaluator.setItes(ites);
        //匹配
        Map<Individual,Individual> pairM = matcher.insAlign(ises,ites);  //得到匹配结果
        evaluator.evaluate(pairM);  //计算评估结果
        String forceRes = "G:\\ExperimentSpace\\noNameRes.txt";  //暴力匹配结果
        String nameRes = "G:\\ExperimentSpace\\nameRes.txt";    //使用了名称筛选的结果
        String nameStruRes = "G:\\ExperimentSpace\\nameStruRes.txt";  //使用了名称和结构信息的结果
        System.out.println(evaluator.getPrecision());
        System.out.println(evaluator.getRecall());
        System.out.println(evaluator.getF1());
    }

}
