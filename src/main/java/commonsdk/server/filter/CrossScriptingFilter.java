package commonsdk.server.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CrossScriptingFilter implements Filter {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CrossScriptingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        RequestWrapper wrappedRequest = new RequestWrapper((HttpServletRequest) request);

        String body = IOUtils.toString(wrappedRequest.getReader());
        if(!"".equals(body)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode rootNode = mapper.readTree(body);
                try {
                    loopThroughJson(rootNode);
                } catch (Exception e) {
                    log.info("Unable to encode Json recursively...");
                }
                wrappedRequest.resetInputStream(rootNode.toString().getBytes());
            } catch (IOException e) {
                String encoded = CrossScriptingUtils.stripXSS(body);
                wrappedRequest.resetInputStream(encoded.getBytes());
            }
        }

        chain.doFilter(wrappedRequest, response);
    }
    public void loopThroughJson(JsonNode rootNode) throws Exception {
        if (rootNode == null) {
            return;
        } else if (rootNode.isObject()) {
            Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();
                String key = field.getKey();
                JsonNode value = field.getValue();

                if (value.isObject()) {
                    loopThroughJson(value);
                } else if (value.isArray()) {
                    loopThroughJson(value);
                } else if (value.isTextual()) {
                    String encodedValue = CrossScriptingUtils.stripXSS(value.asText());
                    ((ObjectNode) rootNode).put(key, encodedValue);
                }
            }
        } else if (rootNode.isArray()) {
            int len = rootNode.size();
            for (int i=0; i<len; i++) {
                JsonNode element = rootNode.get(i);
                if (element.isObject()) {
                    loopThroughJson(element);
                } else if (element.isArray()) {
                    loopThroughJson(element);
                } else if (element.isTextual()) {
                    String encodedElement = CrossScriptingUtils.stripXSS(element.asText());
                    ((ArrayNode) rootNode).remove(i);
                    ((ArrayNode) rootNode).insert(i, encodedElement);
                }
            }
        }
    }

}