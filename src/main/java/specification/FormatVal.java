package specification;

/**
 * Created by The Illsionist on 2019/3/20.
 * 格式化值类
 * 格式化值大体可分为 带单位数值型,日期型,字母数字短文本,中文短文本,其他对于一些特殊属性会有特殊的取值格式
 */
public class FormatVal {

    private String original = null;  //原值
    private int formatID = -1;   //基本格式唯一标识
    private int[] fDates = null;  //日期型,存开始的或仅有的年,月,日(没有为-1)
    private int[] tDates = null;  //日期型,存截止的年,月,日
    private double fNum = -1.0; //若是带单位的数值,存单位标准化后的数值,没有单位,代表所有单位一致
    private double tNum = -1.0; //截止数值

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
        if(this.isDate() && val.isDate())  //两个都是日期型(包括日期范围型),则格式相同
            return true;
        else
            return this.formatID == val.formatID ? true : false;
    }

    /**
     * 设置开始日期
     * @param fDates
     */
    public void setfDates(int[] fDates){
        this.fDates = fDates;
    }

    /**
     * 设置截止日期
     * @param tDates
     */
    public void settDates(int[] tDates){
        this.tDates = tDates;
    }

    public int[] getfDates(){
        return fDates;
    }

    public int[] gettDates(){
        return tDates;
    }

    /**
     * 设置数值下限
     * @param fNum
     */
    public void setfNum(double fNum){
        this.fNum = fNum;
    }

    /**
     * 设置数值上限
     * @param tNum
     */
    public void settNum(double tNum){
        this.tNum = tNum;
    }

    public double getfNum(){
        return fNum;
    }

    public double gettNum(){
        return tNum;
    }

    /**
     * 判断是否是字母和数字组成的短文本
     * TODO:注意这里写死了,后面极其有可能要改
     * @return
     */
    public boolean isLetterStr(){
        return formatID == 10;
    }

    /**
     * 判断是否是文字短文本
     * TODO:注意这里写死了,后面极其有可能要改
     * @return
     */
    public boolean isText(){
        return formatID == 11;
    }

    /**
     * 判断该值是否是带单位的数值,包括数值范围型
     * TODO:注意这里写死了,后面极其有可能要改
     * @return
     */
    public boolean isNum(){
        return formatID >= 1 && formatID <= 7;
    }

    /**
     * 判断该值是否是带单位范围型数值
     * TODO:注意这里写死了,后面极其有可能要改
     * @return
     */
    public boolean isNumRange(){
        if(formatID >= 1 && formatID <= 7 && this.tNum > 0.0)
            return true;
        return false;
    }

    /**
     * 判断该值是否是日期型,包括日期范围型
     * TODO:注意这里写死了,后面极其有可能要改
     * @return
     */
    public boolean isDate(){
        return (formatID == 8 || formatID == 9);
    }

    /**
     * 判断该值是否为日期范围型
     * TODO:注意这里写死了,后面极其有可能要改
     * @return
     */
    public boolean isDateRange(){
        if((formatID == 8 || formatID == 9) && this.tDates != null)
            return true;
        return false;
    }


    /**
     * 返回原值
     * @return
     */
    public String getOriginal(){
        return original;
    }


}
