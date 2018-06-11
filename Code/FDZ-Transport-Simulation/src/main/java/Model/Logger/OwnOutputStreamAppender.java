package Model.Logger;

import ch.qos.logback.core.OutputStreamAppender;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OwnOutputStreamAppender<E> extends OutputStreamAppender {

    private static final DelegatingOutputStream DEL_OUT_SREAM = new DelegatingOutputStream(null);

    @Override
    public void start() {
        setOutputStream(DEL_OUT_SREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DEL_OUT_SREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {

        /**
         * Creates a delegating outputstream with a NO-OP delegate
         */
        public DelegatingOutputStream(OutputStream out){
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {}
            });
        }

        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }
}
