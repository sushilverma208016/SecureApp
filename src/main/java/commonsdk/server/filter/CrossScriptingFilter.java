    package commonsdk.server.filter;

    import java.io.IOException;
    import java.io.OutputStream;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Map;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
    import javax.servlet.Filter;
    import javax.servlet.FilterChain;
    import javax.servlet.FilterConfig;
    import javax.servlet.ServletException;
    import javax.servlet.ServletRequest;
    import javax.servlet.ServletResponse;
    import javax.servlet.annotation.WebFilter;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import commonsdk.server.filter.ResponseWrapper;

    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.node.ArrayNode;
    import com.fasterxml.jackson.databind.node.ObjectNode;
    import org.apache.catalina.connector.ResponseFacade;
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
        public static String csrf;
        private static String id;

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

            OutputStream out = response.getOutputStream();
            ResponseWrapper wrappedResponse = new ResponseWrapper((HttpServletResponse)response);
            String requestURI = ((HttpServletRequest) request).getRequestURI();

            String body = IOUtils.toString(wrappedRequest.getReader());
            if(!"".equals(body)) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode rootNode = mapper.readTree(body);
                    if(requestURI.equals("/api/message/transfer")) {
                        if (!CrossScriptingUtils.csrfVerify(rootNode)) return;
                    }
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


            // // '-->><script> while(1) alert("hacked")</script>
            try {
                chain.doFilter(wrappedRequest, wrappedResponse);
            } catch (Exception exp) {
                exp.printStackTrace();
                throw exp;
            }

            byte responseContent[] = wrappedResponse.getData();
            String changedResponseContent = "";
            if(requestURI.matches("/api/message/login")) {
                getId(responseContent.toString());
            }



            if (response != null && response.getContentType() != null && response.getContentType().equals("text/csv")) {
                changedResponseContent = changeResponse(responseContent.toString());
            } else {
                changedResponseContent = responseContent.toString();
            }
            if(requestURI.equals("/api/message/login")) {
                csrf = CrossScriptingUtils.generateCSRFToken();
            }

            if(requestURI.matches("/api/message/"+id) && !csrf.isEmpty()) {
                changedResponseContent = changedResponseContent.substring(0,changedResponseContent.length()-1) + ",\"csrftoken\":\"" + csrf + "\"}";
            }

            if (response != null && response.getContentType() != null && response.getContentType().contains("application/json") && responseContent != null) {
                String modifiedResponseString = CrossScriptingUtils.unstripString(changedResponseContent);
                byte modifiedResponseContent[] = modifiedResponseString.getBytes("UTF-8");

                response.setContentLength(modifiedResponseContent.length);
                response.setCharacterEncoding("UTF-8");
                out.write(modifiedResponseContent);
            } else {
                response.setContentLength(responseContent.length);
                response.setCharacterEncoding("UTF-8");
                out.write(responseContent);
            }
            out.flush();
            out.close();
        }

        private void getId(String responseContent) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseContent);
            JsonNode jsonid = rootNode.get("id");
            id = jsonid.asText();
        }

        public String changeResponse(String responseContent) {
            if(responseContent == null) return responseContent;
            String removedEqual = responseContent.replace("=", "");
            String removedMinus = removedEqual.replace("-", "");
            String removedPlus = removedMinus.replace("+", "");
            String removedPipe = removedPlus.replace("|", "");
            return removedPipe;
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