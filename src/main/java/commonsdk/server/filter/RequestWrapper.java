package commonsdk.server.filter;

/**
 * Created by vsushil on 10/27/2017.
 */

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestWrapper extends HttpServletRequestWrapper {
    private static final Logger log = LoggerFactory.getLogger(RequestWrapper.class);

    private byte[] rawData;
    private HttpServletRequest request;
    private ResettableServletInputStream servletStream;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletStream = new ResettableServletInputStream();
    }


    public void resetInputStream(byte[] newRawData) {
        servletStream.stream = new ByteArrayInputStream(newRawData);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        }
        return servletStream;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = this.request.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = CrossScriptingUtils.stripXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = this.request.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return CrossScriptingUtils.stripXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = this.request.getHeader(name);
        if (value == null) {
            return null;
        }
        return CrossScriptingUtils.stripXSS(value);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        }
        return new BufferedReader(new InputStreamReader(servletStream));
    }

    private class ResettableServletInputStream extends ServletInputStream {
        private ByteArrayInputStream stream;

        @Override
        public int read() throws IOException {
            return stream.read();
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setReadListener(ReadListener listener) {
            throw new RuntimeException("Not implemented");
        }
        @Override
        public boolean isFinished() {
            return stream.available() == 0;
        }
    }
}