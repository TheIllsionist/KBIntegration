package similarity;

import org.apache.jena.ontology.*;
import org.springframework.beans.factory.annotation.Autowired;
import utils.VDocTFUtil;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/9.
 * 虚拟文档相似度计算器
 */
public class VDocSimilarity implements Similarity{

    private OntModel ks = null;  //源本体
    private OntModel kt = null;  //目标本体

    @Autowired
    private VDocTFUtil tfUtil;

    private Map<String,Integer> baseVec = null;   //向量空间坐标基矢量
    private Map<String,Integer> idf = null;       //每个项的逆文档频率
    private Map<String,Double> localConf = null;  //本地信息提取配置
    private Map<String,Double> neiborConf = null; //周边信息提取配置
    private int docs1 = 0;  //本体1的文档数
    private int docs2 = 0;  //本体2的文档数

    /**
     * 设置源知识库
     * @param ks
     */
    public void setSourceKB(OntModel ks){
        this.ks = ks;
    }

    /**
     * 设置目标知识库
     * @param kt
     */
    public void setTargetKB(OntModel kt){
        this.kt = kt;
    }

    /**
     * 设置读取文档的本地配置
     * @param localConf
     */
    public void setLocalConf(Map<String,Double> localConf){
        this.localConf = localConf;
    }

    /**
     * 设置读取文档的周边信息配置
     * @param neiborConf
     */
    public void setNeiborConf(Map<String,Double> neiborConf){
        this.neiborConf = neiborConf;
    }


    @Override
    public double similarityOf(OntResource res1, OntResource res2) {

    }

    /**
     * 计算两个本体类之间的文档相似度
     * @param cls1
     * @param cls2
     * @return
     */
    private double similarityOf(OntClass cls1,OntClass cls2){

    }

    /**
     * 计算两个本体DP之间的文档相似度
     * @param prop1
     * @param prop2
     * @return
     */
    private double similarityOf(DatatypeProperty prop1,DatatypeProperty prop2){

    }


    /**
     * 计算两个本地OP之间的文档相似度
     * @param prop1
     * @param prop2
     * @return
     */
    private double similarityOf(ObjectProperty prop1, ObjectProperty prop2){

    }

}
