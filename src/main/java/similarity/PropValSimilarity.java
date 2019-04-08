package similarity;

import org.simmetrics.metrics.StringMetrics;
import org.springframework.stereotype.Component;
import specification.FormatVal;

/**
 * 自定义值相似度计算器
 * 计算两个属性值之间的相似度,支持数值型,带单位的数值型,日期型,日期范围型,字母数字短文本,中文短文本
 */
@Component
public class PropValSimilarity implements ValSimilarity{

    /**
     * 计算两个格式化值之间的相似度
     * 在计算之前,已经判断过两个格式化值的格式是否相同了
     * @param v1
     * @param v2
     * @return
     */
    @Override
    public double similarityOf(FormatVal v1,FormatVal v2) throws Exception{
        if(!v1.sameFormatWith(v2))  //检查格式是否相同
            throw new Exception("ERROR : 值 \"" + v1.getOriginal() + "\" 与值 \"" + v2.getOriginal() + " \"之间格式不同,无法计算相似度!");
        double resSim = 0.0;
        if(v1.isDate()){  //两个日期型(可能是范围型)
            boolean isMatch = false;
            if(!v1.isDateRange() && !v2.isDateRange()){  //两个都不是范围型
                isMatch = match2Date(v1,v2);
            }else if(v1.isDateRange() && v2.isDateRange()){
                isMatch = match2DateRange(v1,v2);
            }else{
                isMatch = matchRangeAndDate(v1,v2);
            }
            resSim = isMatch ? 1.0 : 0.0;  //匹配则相似度值为1.0,不匹配相似度为0.0
        }else if(v1.isNum()){  //两个带单位数值型(可能是范围型)
            if(!v1.isNumRange() && !v2.isNumRange()){
                resSim = sim2Num(v1,v2);
            }else if(v1.isNumRange() && v2.isNumRange()){
                resSim = sim2NumRange(v1,v2);
            }else{
                resSim = matchRangeAndNum(v1,v2) ? 1.0 : 0.0;
            }
        }else if(v1.isLetterStr() || v1.isText()){   //两个数字字母短字符串或两个中文字符串
            //当前是基于编辑距离计算两个字符串之间的相似度
            resSim = StringMetrics.levenshtein().compare(v1.getOriginal(),v2.getOriginal());
        }else if(v1.getFormatID() == 18){  //续航距离特殊单位
            String str1 = v1.getOriginal();
            String str2 = v2.getOriginal();
            resSim = str1.equals(str2) ? 1.0 : 0.0;  //两个串相等则匹配
        }
        return resSim;
    }

    /**
     * 计算两个 带单位值 间的相似度
     * @param v1 &nbsp 值1
     * @param v2 &nbsp 值2
     * @return
     */
    private double sim2Num(FormatVal v1,FormatVal v2){
        return simOf2Num(v1.getfNum(),v2.getfNum());
    }

    /**
     * 计算两个 带单位范围值 间的相似度
     * 范围头相似 && 范围尾相似 , 才算相似
     * @param range1 &nbsp 范围1
     * @param range2 &nbsp 范围2
     * @return
     */
    private double sim2NumRange(FormatVal range1,FormatVal range2){
        double fNum1 = range1.getfNum();
        double tNum1 = range1.gettNum();
        double fNum2 = range2.getfNum();
        double tNum2 = range2.gettNum();
        double fSim = simOf2Num(fNum1,fNum2);
        double tSim = simOf2Num(tNum1,tNum2);
        return fSim <= tSim ? fSim : tSim;  //返回那个小的相似度值,如果小的值都超过了阈值,则两个范围匹配
    }

    /**
     * 将一个带单位范围值和一个带单位值进行比较,若该值在这个范围中,就表示匹配,不在表示不匹配
     * @param v1
     * @param v2
     * @return
     */
    private boolean matchRangeAndNum(FormatVal v1,FormatVal v2){
        FormatVal range = v1.gettNum() < 0.0 ? v2 : v1;  //确定哪个是范围值
        FormatVal v = v1.gettNum() < 0.0 ? v1 : v2;     //确定哪个不是范围值
        double val = v.getfNum();
        if(val >= range.getfNum() && val <= range.gettNum()){
            return true;
        }else
            return false;
    }

    /**
     * 判断两个日期值是否匹配
     * @param d1 &nbsp 日期值1
     * @param d2 &nbsp 日期值2
     * @return
     */
    private boolean match2Date(FormatVal d1,FormatVal d2){
        return compare(d1.getfDates(),d2.getfDates()) == 0;
    }

    /**
     * 判断两个日期范围值是否匹配
     * 范围头相似 && 范围尾相似 , 才算相似
     * @param range1
     * @param range2
     * @return
     */
    private boolean match2DateRange(FormatVal range1,FormatVal range2){
        if( compare(range1.getfDates(),range2.getfDates()) == 0
                && compare(range1.gettDates(),range2.gettDates()) == 0 )
            return true;
        else
            return false;
    }

    /**
     * 判断一个日期值和一个日期范围值是否匹配
     * @param d1
     * @param d2
     * @return
     */
    private boolean matchRangeAndDate(FormatVal d1,FormatVal d2){
        FormatVal range = d1.gettDates() == null ? d2 : d1;
        FormatVal v = d1.gettDates() == null ? d1 : d2;
        int[] fDate = range.getfDates();
        int[] tDate = range.gettDates();
        int[] date = v.getfDates();
        if(compare(date,fDate) >= 0 && compare(date,tDate) <= 0)
            return true;
        return false;
    }


    /**
     * 计算两个数值之间的相似度
     * @param num1
     * @param num2
     * @return
     */
    private double simOf2Num(double num1,double num2){
        double abs = Math.abs(num1 - num2);  //两个数值差的绝对值
        abs = (abs / 10) + 1;  //算法需要除以10
        double result = 1.0 / (Math.log(abs)/Math.log(2.0) + 1);
        return result;
    }

    /**
     * 比较两个日期的大小
     * date1 > date2 时,返回1
     * date1 < date2 时,返回-1
     * date1 = date2 时,返回0
     * @param date1
     * @param date2
     * @return
     */
    private int compare(int[] date1,int[] date2){
        if(date1[0] > date2[0])  //date1 > date2
            return 1;
        if(date1[0] < date2[0])  //date1 < date2
            return -1;
        //两者年相等
        if(date1[1] == -1 || date2[1] == -1) //年相等,月无效,则两者相等
            return 0;
        if(date1[1] > date2[1])  //date1 > date2
            return 1;
        if(date1[1] < date2[1])  //date1 < date2
            return -1;
        //两者月相等
        if(date1[2] == -1 || date2[2] == -1) //月相等,日无效,则两者相等
            return 0;
        if(date1[2] > date2[2])  //date1 > date2
            return 1;
        if(date1[2] < date2[2])  //date1 < date2
            return -1;
        return 0;
    }


}
