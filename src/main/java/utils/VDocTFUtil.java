package utils;

import parser.Parser;
import org.apache.jena.ontology.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokenizer.Tokenizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/9.
 */
@Component
public class VDocTFUtil {

    @Autowired
    private Tokenizer tokenizer;  //分词工具
    @Autowired
    private Parser parser;  //信息抽取器


    /**
     * 获取某类虚拟文档中的TF信息,根据信息获取的配置,可能包含该资源的本地信息和周边信息
     * @param model &nbsp 本体
     * @param cls &nbsp 指定的类
     * @param localConf &nbsp 本地信息的权重配置
     * @param neiborConf &nbsp 周边信息的权重配置
     * @return
     * @throws Exception
     */
    public Map<String,Double> tfOfClass(OntModel model, OntClass cls, Map<String,Double> localConf, Map<String,Double> neiborConf) throws Exception{
        if(cls == null)
            throw new NullPointerException();
        Map<String,Double> localTF = localTFMapOf(cls,localConf);   //得到本地TF信息
        for(Map.Entry<String,Double> entry : neiborConf.entrySet()){
            switch (entry.getKey()){
                case "supClass":{    //类的父类作为邻居信息
                    List<OntClass> supClses = parser.subClassesOf(cls);  //所有直接父类
                    for(OntClass tSup : supClses){
                        if(tSup.isAnon() || !tSup.isClass())
                            continue;
                        Map<String,Double> neiMap = localTFMapOf(tSup,localConf);
                        mergeLocMapWithNeiMap(localTF,neiMap,entry.getValue());
                    }
                }break;
                case "subClass":{   //类的子类作为邻居信息
                    List<OntClass> subClses = parser.subClassesOf(cls);  //所有直接子类
                    for(OntClass tSub : subClses){
                        if(tSub.isAnon() || !tSub.isClass())
                            continue;
                        Map<String,Double> neiMap = localTFMapOf(tSub,localConf);
                        mergeLocMapWithNeiMap(localTF,neiMap,entry.getValue());
                    }
                }break;
                case "property":{  //类所"拥有"的属性作为邻居信息
                    List<OntProperty> props = parser.propsOfCls(cls, model, 0.75);
                    for (OntProperty prop : props) {
                        if(prop.isAnon() || (!prop.isDatatypeProperty() && !prop.isObjectProperty())){
                            continue;
                        }
                        Map<String,Double> neiMap = localTFMapOf(prop,localConf);
                        mergeLocMapWithNeiMap(localTF,neiMap,entry.getValue());
                    }
                }break;
            }
        }
        //经过以上步骤,本地信息和邻居信息都已被加入到localTFMap中
        normalization(localTF);  //计算词频
        return localTF;
    }


    /**
     * 计算属性的TF信息,包括本地信息和周边信息
     * @param model
     * @param prop
     * @param localConf
     * @param neiborConf
     * @return
     * @throws Exception
     */
    public Map<String,Double> tfOfProp(OntModel model,OntProperty prop,Map<String,Double> localConf,Map<String,Double> neiborConf) throws Exception{
        if(prop == null)
            throw new NullPointerException();
        Map<String,Double> localTF = localTFMapOf(prop,localConf);
        for(Map.Entry<String,Double> entry : neiborConf.entrySet()){
            switch (entry.getKey()){
                case "domain":{
                    List<OntClass> domains = parser.domainOfProp(prop,model,0.75);
                    for(OntClass cls : domains){
                        if(cls.isAnon() || !cls.isClass())
                            continue;
                        Map<String,Double> neiMap = localTFMapOf(cls,localConf);
                        mergeLocMapWithNeiMap(localTF,neiMap,entry.getValue());
                    }
                }break;
                case "range":{
                    List<OntClass> ranges = parser.rangeOfOp(prop.asObjectProperty(),model,0.75);
                    for(OntClass cls : ranges){
                        if(cls.isAnon() || !cls.isClass())
                            continue;
                        Map<String,Double> neiMap = localTFMapOf(cls,localConf);
                        mergeLocMapWithNeiMap(localTF,neiMap,entry.getValue());
                    }
                }break;
            }
        }
        normalization(localTF); //计算词频
        return localTF;
    }


    /**
     * 提取某个资源的本地TF信息
     * @param res
     * @param localConf &nbsp 提取哪些本地信息以及信息的权重配置
     * @return
     * @throws Exception
     */
    public Map<String,Double> localTFMapOf(OntResource res,Map<String,Double> localConf) throws Exception{
        if(res == null)
            throw new NullPointerException();
        Map<String,Double> tfMap = new HashMap<>();
        //根据配置提取信息
        for (Map.Entry<String,Double> entry : localConf.entrySet()) {
            switch(entry.getKey()){
                case "LABEL":{  //使用rdfs:label信息
                    List<String> labels = parser.labelsOf(res);  //列出该实体的所有可读名称
                    for(String label : labels){
                        List<String> tokens = tokenizer.tokensOfStr(label);  //tokens是允许有重复的
                        addTokensToMap(tokens,tfMap,entry.getValue());
                    }
                }break;
                case "COMMENT":{  //使用rdfs:comment信息
                    List<String> comments = parser.commentsOf(res);  //列出该实体的所有释义描述
                    for(String comment : comments){
                        List<String> tokens = tokenizer.tokensOfStr(comment);  //(分词去频繁词)tokens是允许有重复的
                        addTokensToMap(tokens,tfMap,entry.getValue());
                    }
                }break;
            }
        }
        return tfMap;
    }


    /**
     * 将周边TF信息合并到本地TF信息中
     * @param locMap
     * @param neiMap
     * @param neiWeight &nbsp 周边信息的权重
     */
    private void mergeLocMapWithNeiMap(Map<String,Double> locMap,Map<String,Double> neiMap,double neiWeight){
        for(String key : neiMap.keySet()){
            double neiVal = neiMap.get(key);
            if(locMap.containsKey(key)){    //这个token在本地信息中已存在
                locMap.put(key,locMap.get(key) + neiWeight * neiVal);
            }else{
                locMap.put(key, 0.0 + neiWeight * neiVal);
            }
        }
    }


    /**
     * 细节操作,将一系列tokens按照指定权重weight加入一个tokenMap中
     * @param tokens
     * @param tokenMap
     * @param weight
     */
    private void addTokensToMap(List<String> tokens, Map<String,Double> tokenMap, double weight){
        for (String token : tokens) {
            if(tokenMap.containsKey(token)){
                tokenMap.put(token,tokenMap.get(token) + 1 * weight);
            }else{
                tokenMap.put(token,1 * weight);
            }
        }
    }


    private void normalization(Map<String,Double> map){
        double total = 0;
        for(String key : map.keySet()){
            total += map.get(key);
        }
        for(String key : map.keySet()){
            map.put(key,map.get(key) / total);  //计算出真正的词频(从这里可以看到,虚拟文档改写了TF)
        }
    }

}
