package similarity;

import specification.FormatVal;

/**
 * Created by The Illsionist on 2019/3/22.
 * 值相似度计算器
 * 计算两个属性值之间的相似度,支持数值型,带单位的数值型,日期型,日期范围型,字母数字短文本,中文短文本
 */
public class ValSimilarity {

    private double numThreshold;   //数值相似度阈值
    private double dateThreshold;  //日期相似度阈值
    private double textThreshold;  //文本相似度阈值


    /**
     * 计算两个格式化值之间的相似度
     * @param val1
     * @param val2
     * @return
     */
    public double similarityOf(FormatVal val1,FormatVal val2){
        return 0.0;
    }


}
