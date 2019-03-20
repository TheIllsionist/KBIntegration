package specification;

/**
 * Created by The Illsionist on 2019/3/20.
 * 格式化值类
 * 格式化值大体可分为 带单位数值型,日期型,字母数字短文本,中文短文本,其他对于一些特殊属性会有特殊的取值格式
 */
public class FormatVal {

    private String original = null;  //原值
    private int formatID = -1;   //基本格式唯一标识
    private int[] dates = null;  //若是日期型,存年,月,日(没有为-1)
    private double stdNum = -1.0; //若是带单位的数值,存单位标准化后的数值,没有单位,代表所有单位一致

    public FormatVal(String value){
        this.original = value;
    }

    public void setFormatID(int id){
        this.formatID = id;
    }

    public int getFormatID(){
        return formatID;
    }

    /**
     * 判断两个格式化值是否格式相同
     * @param val
     * @return
     */
    public boolean sameFormatWith(FormatVal val){
        return this.formatID == val.getFormatID();
    }

    /**
     * 判断该值是否是日期型
     * @return
     */
    public boolean isDate(){
        return formatID == 10;  //TODO:注意这里写死了,后面极其有可能要改
    }

    public void setDates(int[] dates){
        this.dates = dates;
    }

    public int[] getDates(){
        return dates;
    }

    /**
     * 判断该值是否是带单位的数值
     * @return
     */
    public boolean isUnitNum(){
        return formatID >= 1 && formatID <= 7;  //TODO:注意这里写死了,后面极其有可能要改
    }

    public void setStdNum(double stdNum){
        this.stdNum = stdNum;
    }

    public double getStdNum(){
        return stdNum;
    }

    /**
     * 判断是否是字母和数字组成的短文本
     * @return
     */
    public boolean isLetterStr(){
        return formatID == 8;         //TODO:注意这里写死了,后面极其有可能要改
    }

    /**
     * 判断是否是文字短文本
     * @return
     */
    public boolean isText(){
        return formatID == 9;       //TODO:注意这里写死了,后面极其有可能要改
    }

    public String getOriginal(){
        return original;
    }

}
