package similarity;

import specification.FormatVal;

/**
 * Created by The Illsionist on 2019/4/6.
 * 值相似度接口
 * 计算两个字面量之间的相似度
 */
public interface ValSimilarity {

    /**
     * 在知识库中的取值都是格式化值,计算两个格式化值之间的相似度
     * @param v1
     * @param v2
     * @return
     */
    double similarityOf(FormatVal v1,FormatVal v2);
}
