package Model.Logger;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private StringBuilder stringBuilder = new StringBuilder();
    private TextArea textArea;

    public TextAreaOutputStream(TextArea textArea){
        this.textArea=textArea;
    }

    @Override
    public void write (int b) throws IOException {
        stringBuilder.append((char)b);
        if(stringBuilder.toString().contains(System.getProperty("line.separator"))){
            textArea.appendText(stringBuilder.toString());
            stringBuilder.delete(0, stringBuilder.length());
        }
    }

}
