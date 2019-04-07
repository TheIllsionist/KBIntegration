import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import similarity.PropValSimilarity;
import similarity.ValSimilarity;
import specification.FormatVal;
import specification.ValFormatSpec;

/**
 * Created by The Illsionist on 2019/4/6.
 */
public class ValSimilarityTest {

    public static void main(String args[]) throws Exception {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
        ValFormatSpec formatSpec = (ValFormatSpec)ioc.getBean("formatSpec");
        ValSimilarity valSimilarity = new PropValSimilarity();
        String val1 = "40.8米";
        String val2 = "40.5米";
        String val3 = "136呎";
        String val4 = "147呎6吋";
        String val5 = "1957年4月24日";
        String val6 = "1957-4-24";
        String val7 = "1957-4-4";
        String val8 = "40米-41米";
        String val9 = "23米-24.5m";
        String val10 = "1993年2月3日-1994年7月3日";
        String val11 = "1993年2月-1994年";
        String val12 = "1993年6月";
        FormatVal fVal1 = formatSpec.formatVal(val1);
        FormatVal fVal2 = formatSpec.formatVal(val2);
        FormatVal fVal3 = formatSpec.formatVal(val3);
        FormatVal fVal4 = formatSpec.formatVal(val4);
        FormatVal fVal5 = formatSpec.formatVal(val5);
        FormatVal fVal6 = formatSpec.formatVal(val6);
        FormatVal fVal7 = formatSpec.formatVal(val7);
        FormatVal fVal8 = formatSpec.formatVal(val8);
        FormatVal fVal9 = formatSpec.formatVal(val9);
        FormatVal fVal10 = formatSpec.formatVal(val10);
        FormatVal fVal11 = formatSpec.formatVal(val11);
        FormatVal fVal12 = formatSpec.formatVal(val12);
        double sim1 = valSimilarity.similarityOf(fVal1,fVal2);
        double sim2 = valSimilarity.similarityOf(fVal3,fVal4);
        double sim3 = valSimilarity.similarityOf(fVal5,fVal6);
        double sim4 = valSimilarity.similarityOf(fVal5,fVal7);
        double sim5 = valSimilarity.similarityOf(fVal1,fVal8);
        double sim6 = valSimilarity.similarityOf(fVal1,fVal9);
        double sim7 = valSimilarity.similarityOf(fVal8,fVal9);
        double sim8 = valSimilarity.similarityOf(fVal5,fVal10);
        double sim9 = valSimilarity.similarityOf(fVal10,fVal11);
        double sim10 = valSimilarity.similarityOf(fVal10,fVal12);
        double sim11 = valSimilarity.similarityOf(fVal11,fVal12);
        System.out.println();
    }

}
