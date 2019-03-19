package specification;

import java.util.Map;

/**
 * Created by The Illsionist on 2019/3/15.
 * 区分性属性值格式规范类
 * 此类对于基于区分性属性计算实例间的相似度非常重要
 * 此类的设计基于人工智能中的人工规则思想
 * 一.类中缓存了军事军备知识库中所有可能的取值格式:包括数值型,带单位(长度,重量,速度等)数值型,日期型,字母数字短文本,中文短文本
 * 二.每一种取值格式都有一个固定的数字编码,作为该取值格式的唯一标识
 * 三.类支持功能：
 *    1.给定一个属性值,能判断当前规范包含的取值格式是否支持对该值的处理
 *    2.对规范支持的属性值,要实现:
 *      a.返回该属性值匹配的取值格式或该格式的唯一标识
 *      b.若该值为带单位的数值,则要支持:值提取,单位提取,返回原值,该单位对应的标准单位值提取
 *      c.若该值为日期型,则要支持:Year提取,Month提取,Day提取
 *      d.若该值为字母数字短文本,则要支持:字符串去除特殊符号(-|_|.等)
 *      e.若该值为中文短文本,支持一种分词方法吧,但不强求分词效果了,可能也没啥用
 *    3.对规范不支持的属性值,要实现:
 *      a.抛出异常信息,信息中包括不支持属性值的原值,以便于对规范中的取值格式进行补充
 * 四.规范基于正则表达式实现
 */
public class ValFormatSpec {

    private Map<String,Integer> formatMap = null;  //区分性属性取值格式(正则表达式,唯一标识)
    private Map<Integer,Map<String,Double>> stdMap = null; //单位标准化(唯一标识,<单位,转换因子>)


    public void setFormatMap(Map<String,Integer> formatMap){
        this.formatMap = formatMap;
    }

    public void setStdMap(Map<Integer,Map<String,Double>> stdMap){
        this.stdMap = stdMap;
    }


    /**
     * 给定属性值,返回当前规范中匹配该属性值的取值格式
     * 若无格式,抛出异常
     * @param value
     * @return
     * @throws Exception
     */
    public String formatOfVal(String value) throws Exception{
        if(value == null)
            throw new Exception("    Error : DP属性取值出现null值!");
        if(value.matches("\\s*"))
            throw new Exception("    Error : DP属性取值出现空串!");
        for(Map.Entry<String,Integer> entry : formatMap.entrySet()){
            if(value.matches(entry.getKey()))
                return entry.getKey();
        }
        throw new Exception("FormatMis : 没有匹配属性值 \"" + value + "\"的取值格式!");
    }

    public int formatIDOfVal(String value) throws Exception{
        String format = formatOfVal(value);
        return formatMap.get(format);
    }

}
