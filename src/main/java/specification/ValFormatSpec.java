package specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by The Illsionist on 2019/3/15.
 * 区分性属性值格式规范类,对于基于区分性属性计算实例间的相似度非常重要
 * 类的设计基于人工规则
 * 一.类中缓存了兵器知识库中所有可能的取值格式:包括数值型,带单位数值型,日期型,带单位数值范围型,日期范围型,字母数字短文本,中文短文本
 * 二.每一种取值格式都有一个固定的数字编码,作为该取值格式的唯一标识
 * 三.类支持功能：
 *    1.对于不支持的属性值格式,抛出异常说明
 *    2.支持的属性值,要提取出该属性值中所有可能被用到的值,并且做单位标准化
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
        for(Map.Entry<String,Integer> entry : formatMap.entrySet()){ //遍历取值格式
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
        return (double)Math.round(stdNum * 100)/100; //四舍五入,两位小数
    }


    /**
     * 根据取值格式和单位,返回将当前值转为对应的标准单位值的转换因子
     * @param basicId
     * @param unit
     * @return
     */
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
        String regex = formatOfVal(val);   //初步取值格式获取,可能是组合格式
        int id = formatMap.get(regex);   //初步取值格式标识
        String basicRegex = combineRel.containsKey(id) ? combineRel.get(id) : regex;  //对于组合格式,找到它对应的基本格式
        int basicId = formatMap.get(basicRegex);
        FormatVal formatVal = new FormatVal(val);
        formatVal.setFormatID(basicId);  //格式ID设为基本格式ID
        Pattern pattern = Pattern.compile(basicRegex);
        if(basicId == 8 || basicId == 9){    //日期型格式
            if(id == 17){  //日期范围
                val = val.replace('~','-');
                int[] fDate = datesOf(val.substring(0,val.indexOf("-")),pattern);
                int[] tDate = datesOf(val.substring(val.indexOf("-") + 1),pattern);
                formatVal.setfDates(fDate);
                formatVal.settDates(tDate);
            }else{  //单纯日期
                formatVal.setfDates(datesOf(val,pattern));
            }
        }else if(basicId >= 1 && basicId <= 7){  //带单位数值型格式
            if(id > 12&& id <= 16){  //范围型
                val = val.replace('~','-');
                String spVals[] = val.split("-");
                Matcher tmpMatcher = pattern.matcher(spVals[1]);
                tmpMatcher.find();
                String unit = tmpMatcher.group("unit");
                char c = spVals[0].charAt(spVals[0].length() - 1);
                if(c >= '0' && c <= '9'){
                    spVals[0] = spVals[0] + unit;
                }
                formatVal.setfNum(stdValOf(spVals[0],basicId,pattern));
                formatVal.settNum(stdValOf(spVals[1],basicId,pattern));
            }else{ //非范围型
                formatVal.setfNum(stdValOf(val,basicId,pattern));
            }
        }else if(basicId == 8){ //字母,数字,中划线和下划线组成的串

        }else if(basicId == 9){ //中文文本串

        }
        return formatVal;
    }

}
