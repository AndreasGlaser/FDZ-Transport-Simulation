package Model.Logger;

import ch.qos.logback.core.OutputStreamAppender;
import java.io.FilterOutputStream;
import java.io.OutputStream;

public class OwnOutputStreamAppender<E> extends OutputStreamAppender {

    private static final DelegatingOutputStream DEL_OUT_STREAM = new DelegatingOutputStream();

    @Override
    public void start() {
        setOutputStream(DEL_OUT_STREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DEL_OUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {

        /**
         * Creates a delegating output stream with a NO-OP delegate
         */
        private DelegatingOutputStream(){
            super(new OutputStream() {
                @Override
                public void write(int b)  {}
            });
        }

        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }
}
