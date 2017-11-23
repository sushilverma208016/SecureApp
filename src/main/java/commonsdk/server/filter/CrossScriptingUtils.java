package commonsdk.server.filter;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * Created by vsushil on 10/30/2017.
 */
public class CrossScriptingUtils {

    private CrossScriptingUtils() {
    }

    public static String stripXSS(String value) {
        return value == null ? value : escapeHtml4(value);
    }
}