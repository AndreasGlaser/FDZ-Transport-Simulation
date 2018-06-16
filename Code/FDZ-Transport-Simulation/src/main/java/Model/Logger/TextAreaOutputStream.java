package Model.Logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private StringBuilder stringBuilder = new StringBuilder();
    private TextArea textArea;

    public TextAreaOutputStream(TextArea textArea){
        this.textArea=textArea;
    }

    /**
     *
     * @param aByte the given byte that will be written, the bytes will be buffered until a line separator is given
     * @throws IOException
     */
    @Override
    public void write (int aByte) throws IOException {
        stringBuilder.append((char)aByte);
        if(stringBuilder.toString().contains(System.getProperty("line.separator"))){
            String output = stringBuilder.toString();
            Platform.runLater(() ->{
                textArea.appendText(output);
            });
            stringBuilder.delete(0, stringBuilder.length());
        }
    }

}
