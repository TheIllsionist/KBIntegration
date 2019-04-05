import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import specification.FormatVal;
import specification.ValFormatSpec;

/**
 * Created by The Illsionist on 2019/3/20.
 */
public class RegexTest {

    public static void main(String args[]) throws Exception{
        ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
        ValFormatSpec formatSpec = (ValFormatSpec)ioc.getBean("formatSpec");
        String val1 = "40.8米";
        String val2 = "33节";
        String val3 = "3583人";
        String val4 = "136呎";
        String val5 = "1957年4月24日";
        String val6 = "60100吨";
        String val7 = "中途岛级";
        String val8 = "CVB-43";
        String val9 = "29.0m";
        String val10 = "147呎6吋";
        String val11 = "34千米/秒";
        String val12 = "34.2m/s";
        String val13 = "6.34马赫";
        String val14 = "20.4-40.9米";
        String val15 = "20.3米-24十米";
        String val16 = "1993年2月3日-1994年7月3日";
        String val17 = "1993年5月-2001年";
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
        FormatVal fVal13 = formatSpec.formatVal(val13);
        FormatVal fVal14 = formatSpec.formatVal(val14);
        FormatVal fVal15 = formatSpec.formatVal(val15);
        FormatVal fVal16 = formatSpec.formatVal(val16);
        FormatVal fVal17 = formatSpec.formatVal(val17);
        System.out.println();
    }

}
