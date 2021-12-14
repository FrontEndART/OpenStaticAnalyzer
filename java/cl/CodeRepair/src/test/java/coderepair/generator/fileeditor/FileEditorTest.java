package coderepair.generator.fileeditor;

import java.io.IOException;

import coderepair.generator.fileeditor.FileEditorUtil.FilePart;
import coderepair.generator.fileeditor.FileEditorUtil.Position;
import org.junit.Test;

public class FileEditorTest {

    @Test
    public void testNormal1() throws IOException {
        String filePath = "src/test/inputs/generatorTests/fileeditor/FileEditorExample.txt";

        FileEditor editor = new FileEditor(filePath, 4);
        FilePart part = new FilePart(filePath, new Position(10, 9), new Position(10, 21));
        String newContent = "Egysoros";

        editor.write(part, newContent);

        part = new FilePart(filePath, new Position(14, 23), new Position(14, 23));
        newContent = "1234\n";

        editor.write(part, newContent);

        part = new FilePart(filePath, new Position(20, 16), new Position(20, 16));
        newContent = "Egys\noros";

        editor.write(part, newContent);

        part = new FilePart(filePath, new Position(26, 13), new Position(26, 13));
        newContent = "Vege";

        editor.write(part, newContent);

        part = new FilePart(filePath, new Position(26, 13), new Position(26, 13));
        newContent = "Vege2";

        editor.write(part, newContent);
    }

}
