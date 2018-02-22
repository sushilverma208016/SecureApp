package commonsdk.server.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream output;
    private int contentLength;
    private String contentType;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        output=new ByteArrayOutputStream();
    }

    public byte[] getData() {
        return output.toByteArray();
    }

    public ByteArrayOutputStream getOutput() {
        return output;
    }

    public ServletOutputStream getOutputStream() {
        return new CopyServletOutputStream(output);
    }

    public PrintWriter getWriter() {
        return new PrintWriter(getOutputStream(),true);
    }

    public void setContentLength(int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentType(String type) {
        this.contentType = type;
        super.setContentType(type);
    }

    public String getContentType() {
        return contentType;
    }

    private class CopyServletOutputStream extends ServletOutputStream {
        private DataOutputStream stream;
        private String copy;

        public CopyServletOutputStream(OutputStream output) {
            stream = new DataOutputStream(output);
            copy = new String();
        }

        public void write(int b) throws IOException {
            stream.write(b);
            copy += b;
        }

        public void write(byte[] b) throws IOException  {
            stream.write(b);
            copy += b.toString();
        }

        public void write(byte[] b, int off, int len) throws IOException  {
            stream.write(b,off,len);
            copy += b.toString();
        }

        public String getCopy() {
            return copy;
        }

        public void setWriteListener(WriteListener arg0) {
        }

        public boolean isReady() {
            return true;
        }
    }
}