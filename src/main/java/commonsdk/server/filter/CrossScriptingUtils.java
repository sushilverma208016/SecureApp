package commonsdk.server.filter;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static commonsdk.server.filter.CrossScriptingFilter.csrf;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * Created by vsushil on 10/30/2017.
 */
public class CrossScriptingUtils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CrossScriptingFilter.class);

    private CrossScriptingUtils() {
    }

    public static String stripXSS(String value) {
        return value == null ? value : escapeHtml4(value);
    }

    public static String generateString(int length)
    {
        char[] chars = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String generateCSRFToken() {
        return generateString(32);
    }

    public static boolean csrfVerify(JsonNode rootNode) {
        try{
            JsonNode csrftoken = rootNode.get("csrftoken");
            String s = csrftoken.asText();
            if (!s.equals(csrf)) {
                log.info("Unauthorized request");
                return false;
            }
        }
        catch (NullPointerException ne){
            log.info("Unauthorized request");
            return false;
        }
        return true;
    }
}