package commonsdk.server.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class ResponseWrapper extends HttpServletResponseWrapper {

    ByteArrayOutputStream output;
    FilterServletOutputStream filterOutput;
    HttpResponseStatus status = HttpResponseStatus.OK;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (filterOutput == null) {
            filterOutput = new FilterServletOutputStream(output);
        }
        return filterOutput;
    }

    public byte[] getDataStream() {
        return output.toByteArray();
    }
}

