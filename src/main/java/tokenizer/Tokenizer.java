package tokenizer;

import java.util.List;

/**
 * Created by The Illsionist on 2019/3/8.
 */
public interface Tokenizer {

    List<String> tokensOfStr(String str);  //tokens是有重复的

}
