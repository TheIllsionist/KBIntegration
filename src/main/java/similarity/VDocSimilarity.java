package similarity;

import org.apache.jena.ontology.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.springframework.beans.factory.annotation.Autowired;
import utils.VDocTFUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by The Illsionist on 2019/3/9.
 * 虚拟文档相似度计算器
 * TODO:如果主要依靠区分性属性值进行相似度计算,那么虚拟文档相似度计算器可能用不上,当前计算器未经过测试
 */
public class VDocSimilarity implements Similarity {

    //两个知识库
    private OntModel ks = null;  //源本体
    private OntModel kt = null;  //目标本体

    @Autowired
    private VDocTFUtil tfUtil;  //tf信息抽取工具

    private Map<String,Map<String,Double>> ksTFMaps = null;  //源本体中资源TF信息的缓存
    private Map<String,Map<String,Double>> ktTFMaps = null;  //目标本体中资源TF信息的缓存
    private Map<String,Integer> clsBsVec = null;   //类匹配向量空间坐标基矢量
    private Map<String,Integer> dpBsVec = null;    //dp匹配向量空间坐标基矢量
    private Map<String,Integer> opBsVec = null;    //op匹配向量空间坐标基矢量
    private Map<String,Integer> clsIdfInfo = null;  //类匹配时每个项的逆文档频率
    private Map<String,Integer> dpIdfInfo = null;   //DP匹配时每个项的逆文档频率
    private Map<String,Integer> opIdfInfo = null;   //OP匹配时每个项的逆文档频率
    private Map<String,Double> localConf = null;    //本地信息提取配置
    private Map<String,Double> clsNbConf = null;    //类匹配时周边信息提取配置
    private Map<String,Double> dpNbConf = null;   //dp属性匹配时周边信息提取配置
    private Map<String,Double> opNbConf = null;   //op属性匹配时周边信息提取配置
    private int clsM = 0;  //本体1中类数目
    private int clsN = 0;  //本体2中类数目
    private int dpM = 0;   //本体1中DP数目
    private int dpN = 0;   //本体2中DP数目
    private int opM = 0;   //本体1中OP数目
    private int opN = 0;   //本体2中OP数目

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
     * 设置读取类文档的周边信息配置
     * @param clsNbConf
     */
    public void setClsNbConf(Map<String,Double> clsNbConf){
        this.clsNbConf = clsNbConf;
    }

    /**
     * 设置读取DP文档的周边信息
     * @param dpNbConf
     */
    public void setDpNbConf(Map<String,Double> dpNbConf){
        this.dpNbConf = dpNbConf;
    }

    /**
     * 设置读取OP文档的周边信息
     * @param opNbConf
     */
    public void setOpNbConf(Map<String,Double> opNbConf){
        this.opNbConf = opNbConf;
    }

    /**
     * 计算两个资源的文档相似度
     * @param res1
     * @param res2
     * @return
     */
    @Override
    public double similarityOf(OntResource res1, OntResource res2) throws Exception{
        if(res1 == null || res2 == null)  //其中一个为null,相似度为0
            return 0.0;
        if(res1.isClass()){  //类
            return similarityOf(res1.asClass(),res2.asClass());
        }else if(res1.isDatatypeProperty()){  //数据类型属性
            return similarityOf(res1.asDatatypeProperty(),res2.asDatatypeProperty());
        }else{    //对象属性
            return similarityOf(res1.asObjectProperty(),res2.asObjectProperty());
        }
    }

    /**
     * 计算两个本体类之间的文档相似度
     * @param cls1
     * @param cls2
     * @return
     */
    private double similarityOf(OntClass cls1,OntClass cls2) throws Exception{
        if(clsBsVec == null){
            initClsMathchInfo();
        }
        Map<String,Double> VDTFMap1 = ksTFMaps.get(cls1.getURI());  //文档1
        Map<String,Double> VDTFMap2 = ktTFMaps.get(cls2.getURI());  //文档2
        double[] vec1 = convertVD2Vec(VDTFMap1,(clsM + clsN),clsIdfInfo,clsBsVec);  //将文档1转换为向量,转换过程中计算 tf * idf
        double[] vec2 = convertVD2Vec(VDTFMap2,(clsM + clsN),clsIdfInfo,clsBsVec);  //将文档2转换为向量,转换过程中计算 tf * idf
        return simOf2Vec(vec1,vec2);
    }


    /**
     * 计算两个本体DP之间的文档相似度
     * @param dp1
     * @param dp2
     * @return
     */
    private double similarityOf(DatatypeProperty dp1,DatatypeProperty dp2) throws Exception{
        if(dpBsVec == null){
            initDpMatchInfo();
        }
        Map<String,Double> VDTFMap1 = ksTFMaps.get(dp1.getURI());  //文档1
        Map<String,Double> VDTFMap2 = ktTFMaps.get(dp2.getURI());  //文档2
        double[] vec1 = convertVD2Vec(VDTFMap1,(dpM + dpN),dpIdfInfo,dpBsVec);  //将文档1转换为向量,转换过程中计算 tf * idf
        double[] vec2 = convertVD2Vec(VDTFMap2,(dpM + dpN),dpIdfInfo,dpBsVec);  //将文档2转换为向量,转换过程中计算 tf * idf
        return simOf2Vec(vec1,vec2);
    }


    /**
     * 计算两个本地OP之间的文档相似度
     * @param op1
     * @param op2
     * @return
     */
    private double similarityOf(ObjectProperty op1, ObjectProperty op2) throws Exception{
        if(opBsVec == null){
            initOpMatchInfo();
        }
        Map<String,Double> VDTFMap1 = ksTFMaps.get(op1.getURI());  //文档1
        Map<String,Double> VDTFMap2 = ktTFMaps.get(op2.getURI());  //文档2
        double[] vec1 = convertVD2Vec(VDTFMap1,(opM + opN),opIdfInfo,opBsVec);  //将文档1转换为向量,转换过程中计算 tf * idf
        double[] vec2 = convertVD2Vec(VDTFMap2,(opM + opN),opIdfInfo,opBsVec);  //将文档2转换为向量,转换过程中计算 tf * idf
        return simOf2Vec(vec1,vec2);
    }


    /**
     * 计算两个向量之间的余弦相似度
     * @param vec1
     * @param vec2
     * @return
     */
    private double simOf2Vec(double[] vec1,double[] vec2){
        if(vec1 == null || vec2 == null || vec1.length == 0 || vec2.length == 0 || vec1.length != vec2.length)
            return 0.0;
        double target = 0.0;   //最终结果
        double fenzi = 0.0;    //计算分子
        double sumVec1 = 0.0;  //分母的一部分
        double sumVec2 = 0.0;  //分母的另一部分
        for(int i = 0;i < vec1.length;i++){
            if(vec1[i] == 0 || vec2[i] == 0){
                if (vec1[i] != 0) {
                    sumVec1 += vec1[i] * vec1[i];
                }
                if (vec2[i] != 0) {
                    sumVec2 += vec2[i] * vec2[i];
                }
            }else{
                fenzi += vec1[i] * vec2[i];   //TODO: soft TF/IDF的不同是不是关键就在这里?
                sumVec1 += vec1[i] * vec1[i];
                sumVec2 += vec2[i] * vec2[i];
            }
        }
        if(sumVec1 == 0 || sumVec2 == 0)
            target = 0.0;
        else
            target = (fenzi / (Math.sqrt(sumVec1) * Math.sqrt(sumVec2)));
        return Double.valueOf(String.format("%.3f",target));  //取小数点后3位
    }


    /**
     * 给定全局IDF信息和资源的TF信息,得到向量
     * @param VDTFMap
     * @param docNum
     * @param idfInfo
     * @param bsVec
     * @return
     */
    private double[] convertVD2Vec(Map<String,Double> VDTFMap, int docNum, Map<String,Integer> idfInfo, Map<String,Integer> bsVec){
        double[] vec = new double[bsVec.size()];  //向量维数和基矢量维数相等
        int index = 0;
        for(String token : VDTFMap.keySet()){
            double TF = VDTFMap.get(token);     //该token的tf
            double IDF = Math.log((double)docNum / (idfInfo.get(token) + 1)); //该token的idf
            index = bsVec.get(token);  //得到该token对应的维度
            vec[index] = TF * IDF;
        }
        return vec;
    }


    /**
     * 初始化进行类匹配所需要的所有信息,包括:
     * 1.类匹配时的向量空间坐标基矢量
     * 2.类匹配时坐标基矢量中每个词的逆文档频率
     * 3.在计算的过程中,会缓存为每个实体计算出来的TF信息以避免重复计算
     */
    private void initClsMathchInfo() throws Exception{
        Set<String> clsBsSet = new HashSet<>();
        ExtendedIterator<OntClass> ksClses = ks.listClasses();
        while(ksClses.hasNext()){
            OntClass cls = ksClses.next();
            clsInfoGenerate(cls,true,clsBsSet);
        }
        ExtendedIterator<OntClass> ktClses = kt.listClasses();
        while(ktClses.hasNext()){
            OntClass cls = ktClses.next();
            clsInfoGenerate(cls,false,clsBsSet);
        }
        clsBsVec = setToVec(clsBsSet);  //将唯一单词集合转换为类匹配向量空间的坐标基矢量
    }


    /**
     * 信息生成,提取类的TF信息,将其TF信息放入缓存,同时更新对应的逆文档频率
     * @param cls
     * @param fromSource
     * @param bsSet
     */
    private void clsInfoGenerate(OntClass cls, boolean fromSource, Set<String> bsSet) throws Exception{
        Map<String,Double> tfMap = tfUtil.tfOfClass(fromSource ? ks : kt, cls, localConf, clsNbConf); //计算tf信息
        if(fromSource) clsM++;  //更新文档数
        else clsN++;
        //将TF放入缓存
        if(fromSource) ksTFMaps.put(cls.getURI(),tfMap);
        else ktTFMaps.put(cls.getURI(),tfMap);
        bsSet.addAll(tfMap.keySet()); //加入所有唯一项
        //更新IDF信息
        for(String unique : tfMap.keySet()){
            if(clsIdfInfo.containsKey(unique)){
                clsIdfInfo.put(unique,clsIdfInfo.get(unique) + 1);
            }else{
                clsIdfInfo.put(unique,1);
            }
        }
    }

    /**
     * 初始化进行DP匹配所需要的所有信息,包括:
     * 1.向量空间坐标基矢量
     * 2.坐标基矢量中每个词的逆文档频率
     * 3.在计算过程中,会缓存为每个DP计算出的TF信息
     * @throws Exception
     */
    private void initDpMatchInfo() throws Exception{
        Set<String> dpBsSet = new HashSet<>();
        ExtendedIterator<DatatypeProperty> ksDps = ks.listDatatypeProperties();
        while(ksDps.hasNext()){
            DatatypeProperty dp = ksDps.next();
            dpInfoGenerate(dp,true,dpBsSet);
        }
        ExtendedIterator<DatatypeProperty> ktDps = kt.listDatatypeProperties();
        while(ktDps.hasNext()){
            DatatypeProperty dp = ktDps.next();
            dpInfoGenerate(dp,false,dpBsSet);
        }
        dpBsVec = setToVec(dpBsSet);
    }

    /**
     * 初始化进行OP匹配所需要的所有信息,包括:
     * 1.向量空间坐标基矢量
     * 2.坐标基矢量中每个词的逆文档频率
     * 3.在计算过程中,会缓存为每个OP计算出的TF信息
     * @throws Exception
     */
    private void initOpMatchInfo() throws Exception{
        Set<String> opBsSet = new HashSet<>();
        ExtendedIterator<ObjectProperty> ksOps = ks.listObjectProperties();
        while(ksOps.hasNext()){
            ObjectProperty op = ksOps.next();
            opInfoGenerate(op,true,opBsSet);
        }
        ExtendedIterator<ObjectProperty> ktOps = kt.listObjectProperties();
        while(ktOps.hasNext()){
            ObjectProperty op = ktOps.next();
            opInfoGenerate(op,false,opBsSet);
        }
        opBsVec = setToVec(opBsSet);
    }

    /**
     * 信息生成,提取DP的TF信息,将其TF信息放入缓存,同时更新对应的逆文档频率
     * @param dp
     * @param fromSource
     * @param bsSet
     * @throws Exception
     */
    private void dpInfoGenerate(DatatypeProperty dp, boolean fromSource, Set<String> bsSet) throws Exception{
        Map<String,Double> tfMap = tfUtil.tfOfProp(fromSource ? ks : kt, dp,localConf,dpNbConf); //计算tf信息
        if(fromSource) dpM++;  //更新文档数
        else dpN++;
        //放入缓存
        if(fromSource) ksTFMaps.put(dp.getURI(),tfMap);
        else ktTFMaps.put(dp.getURI(),tfMap);
        bsSet.addAll(tfMap.keySet());  //加入所有唯一项
        //更新IDF信息
        for(String unique : tfMap.keySet()){
            if(dpIdfInfo.containsKey(unique)){
                dpIdfInfo.put(unique,dpIdfInfo.get(unique) + 1);
            }else{
                dpIdfInfo.put(unique,1);
            }
        }
    }

    /**
     * 信息生成,提取OP对应的TF信息,将其TF信息放入缓存,同时更新对应的逆文档频率
     * @param op
     * @param fromSource
     * @param bsSet
     * @throws Exception
     */
    private void opInfoGenerate(ObjectProperty op, boolean fromSource, Set<String> bsSet) throws Exception{
        Map<String,Double> tfMap = tfUtil.tfOfProp(fromSource ? ks : kt,op,localConf,opNbConf);  //计算tf信息
        if(fromSource) opM++;  //更新文档数
        else opN++;
        //放入缓存
        if(fromSource) ksTFMaps.put(op.getURI(),tfMap);
        else ktTFMaps.put(op.getURI(),tfMap);
        bsSet.addAll(tfMap.keySet());  //加入所有唯一项
        //更新IDF信息
        for(String unique : tfMap.keySet()){
            if(opIdfInfo.containsKey(unique)){
                opIdfInfo.put(unique,opIdfInfo.get(unique) + 1);
            }else
                opIdfInfo.put(unique,1);
        }
    }

    /**
     * 将唯一单词集合转为一个向量空间的坐标基矢量
     * @param set
     * @return
     */
    private Map<String,Integer> setToVec(Set<String> set){
        Map<String,Integer> map = new HashMap<>();
        int index = 0;
        for(String unique : set){
            map.put(unique,index++);
        }
        return map;
    }

}
