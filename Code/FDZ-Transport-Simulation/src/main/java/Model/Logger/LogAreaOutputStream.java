package Model.Logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.OutputStream;

public class LogAreaOutputStream extends OutputStream {

    private final StringBuilder stringBuilder = new StringBuilder();
    private final TextArea textArea;

    public LogAreaOutputStream(TextArea textArea){
        this.textArea=textArea;
    }

    /**
     * @param aByte the given byte that will be written, the bytes will be buffered until a line separator is given
     */
    @Override
    public void write (int aByte) {
        stringBuilder.append((char)aByte);
        if(stringBuilder.toString().contains(System.getProperty("line.separator"))){
            String output = stringBuilder.toString();
            Platform.runLater(() -> textArea.appendText(output));
            stringBuilder.delete(0, stringBuilder.length());
        }
    }

}
