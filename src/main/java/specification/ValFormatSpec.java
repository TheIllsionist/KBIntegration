package specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Map<String,Integer> formatMap = null;  //取值格式(格式,标识)
    private Map<Integer,String> combineRel = null;  //对于取值格式中的组合格式,需注明组合关系
    private Map<Integer,Map<String,Double>> stdMap = null; //单位标准化(标识,<单位,转换因子>)

    public void setFormatMap(Map<String,Integer> formatMap){
        this.formatMap = formatMap;
    }

    public void setStdMap(Map<Integer,Map<String,Double>> stdMap){
        this.stdMap = stdMap;
    }

    public void setCombineRel(Map<Integer,String> combineRel){
        this.combineRel = combineRel;
    }

    /**
     * 属性值检查,遇到脏数据(null,空串等)抛异常
     * @param value
     * @throws Exception
     */
    private void isValLegal(String value) throws Exception{
        if(value == null)
            throw new Exception("    Error : DP属性取值出现null值!");
        if(value.matches("\\s*"))
            throw new Exception("    Error : DP属性取值出现空串!");
    }

    /**
     * 给定属性值,返回当前规范中匹配该属性值的取值格式
     * 若无格式,抛出异常
     * @param value
     * @return
     * @throws Exception
     */
    private String formatOfVal(String value) throws Exception{
        isValLegal(value);    //属性值合法性检查
        for(Map.Entry<String,Integer> entry : formatMap.entrySet()){ //是否匹配基本格式
            if(value.matches(entry.getKey()))
                return entry.getKey();
        }
        for(Map.Entry<String,Integer> entry : formatMap.entrySet()){  //是否匹配组合格式
            if(value.matches(entry.getKey()))
                return entry.getKey();
        }
        throw new Exception("FormatMis : 没有匹配属性值 \"" + value + "\"的取值格式!");
    }

    /**
     * 将一个日期型值的年,月,日依此提取出来
     * @param value
     * @return
     */
    private int[] datesOf(String value,Pattern pattern){
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        int[] dates = new int[]{-1,-1,-1};
        dates[0] = Integer.valueOf(matcher.group("year"));
        try{
            dates[1] = Integer.valueOf(matcher.group("month"));
            dates[2] = Integer.valueOf(matcher.group("day"));
        }catch (Exception e){
        }
        return dates;
    }

    /**
     * 将一个带单位数值对应的标准单位值计算出来
     * @param value
     * @param pattern
     * @return
     */
    private double stdValOf(String value,int basicId,Pattern pattern){
        Matcher matcher = pattern.matcher(value);
        if(stdMap.get(basicId) == null){
            matcher.find();
            return Double.valueOf(matcher.group("num"));
        }
        List<String> matches = new ArrayList<>(); //先用基本格式提取所有匹配子串(大多数情况下就是原串)
        int sPos = 0;
        do{
            if(!matcher.find(sPos))
                break;
            String tR = matcher.group("result");
            matches.add(tR);
            sPos = matcher.end("result");
        }while(sPos < value.length());
        double stdNum = 0.0;
        for(String str : matches){
            matcher = pattern.matcher(str);
            matcher.find();
            double num = Double.valueOf(matcher.group("num"));
            String unit = matcher.group("unit");
            stdNum += num * getFactor(basicId,unit);
        }
        return stdNum;
    }

    private double getFactor(int basicId,String unit){
        Map<String,Double> map = stdMap.get(basicId);
        for(Map.Entry<String,Double> entry : map.entrySet()){
            if(unit.matches(entry.getKey()))
                return entry.getValue();
        }
        return 1.0;  //找不到对应单位的转换因子,返回原值
    }

    /**
     * 工厂方法,根据值格式,构造该值对应的格式化值对象
     * 若值不合法或者没有该值对应的格式,抛出异常
     * @param val
     * @return
     * @throws Exception
     */
    public FormatVal formatVal(String val) throws Exception{
        String regex = formatOfVal(val);
        int id = formatMap.get(regex);
        String basicRegex = combineRel.containsKey(id) ? combineRel.get(id) : regex;  //若该值的格式为组合格式,找到它对应的基本格式
        int basicId = formatMap.get(basicRegex);
        FormatVal formatVal = new FormatVal(val);
        formatVal.setFormatID(basicId);  //一定是基本格式ID
        Pattern pattern = Pattern.compile(basicRegex);
        if(basicId == 10){    //日期型
            formatVal.setDates(datesOf(val,pattern));
        }else if(basicId >= 1 && basicId <= 7){  //带单位的数值
            formatVal.setStdNum(stdValOf(val,basicId,pattern));
        }else if(basicId == 8){ //字母,数字,中划线和下划线组成的串

        }else if(basicId == 9){ //中文文本串

        }
        return formatVal;
    }

}
