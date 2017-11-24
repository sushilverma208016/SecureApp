package commonsdk.server.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ResponseWrapper extends HttpServletResponseWrapper {

    ByteArrayOutputStream output;
    HttpResponseStatus status = HttpResponseStatus.OK;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new FilterServletOutputStream(output));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new FilterServletOutputStream(output);
    }

    public byte[] getDataStream() {
        return output.toByteArray();
    }
}

