package coderepair.generator.fileeditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The FileEditor class for inserts and deletes slices from a file.
 */
public class FileEditor {

    final private FilePositionUpdater posUpdater;
    final private List<String> strList;
    final private int tabSize;

    /**
     * The Constructor for FileEditor.
     * 
     * @param filePath The URI path of the file we edit.
     * @param tabSize The tab size of the position.
     * @throws IOException If the file is not exists.
     */
    public FileEditor(URI filePath, int tabSize) throws IOException {
        super();
        this.posUpdater = new FilePositionUpdater(tabSize);
        this.strList = fileIntoLines(filePath);
        this.tabSize = tabSize;
    }

    /**
     * The another constructor for FileEditor.
     * 
     * @param filePath The path of the file we edit.
     * @param tabSize The tab size of the position.
     * @throws IOException If the file is not exists.
     */
    public FileEditor(String filePath, int tabSize) throws IOException {
        this(new File(filePath).getAbsoluteFile().toURI(), tabSize);
    }

    /**
     * Overwrites a part of the file.
     * 
     * @param part The part of the file.
     * @param newContent The new content of the part.
     */
    public void write(FileEditorUtil.FilePart part, String newContent) {
        this.write(part, newContent, FilePositionUpdater.OffsetList.DEFAULT_ISAFTER);
    }

    /**
     * Overwrites a part of the file.
     * 
     * @param part The part of the file.
     * @param newContent The new content of the part.
     * @param isAfter If true, the slice will inserted after the previous insert.
     */
    public void write(FileEditorUtil.FilePart part, String newContent, boolean isAfter) {
        boolean isNullSizePos = part.getBeginPos().equals(part.getEndPos());

        FileEditorUtil.Position newBeginPos = this.posUpdater.getNewPosition(part.getBeginPos(), isAfter);
        FileEditorUtil.Position newEndPos = this.posUpdater.getNewPosition(part.getEndPos(), isNullSizePos && isAfter);

        delete(newBeginPos, newEndPos);
        write(newBeginPos, newContent);

        this.posUpdater.addNewOffsets(part, newContent, isAfter);
    }

    /**
     * Overwrites a part of the file.
     * 
     * @param newBeginPos The Position of the file.
     * @param newContent The new Content.
     */
    private void write(FileEditorUtil.Position newBeginPos, String newContent) {
        String line = getLine(newBeginPos.getLine());
        int colNum = FileEditorUtil.getWideColPosition(line, this.tabSize, newBeginPos.getCol());

        if (newContent.indexOf('\n') == -1) {
            StringBuilder builder = new StringBuilder();

            builder.append(line.substring(0, colNum));
            builder.append(newContent);
            builder.append(line.substring(colNum));

            this.strList.set(newBeginPos.getLine() - 1, builder.toString());
        } else {
            String aktLine;
            boolean first = true;
            int lineNum = newBeginPos.getLine() - 1;
            int index = -1;
            int lastIndex = index;

            while ((index = newContent.indexOf('\n', index + 1)) != -1) {
                aktLine = newContent.substring(lastIndex + 1, index);
                if (!first) {
                    this.strList.add(lineNum, aktLine);
                } else {
                    StringBuilder builder = new StringBuilder();

                    builder.append(line.substring(0, colNum));
                    builder.append(aktLine);

                    this.strList.set(lineNum, builder.toString());
                    first = false;
                }

                lastIndex = index;
                lineNum++;
            }

            aktLine = newContent.substring(lastIndex + 1);

            StringBuilder builder = new StringBuilder();

            builder.append(aktLine);
            builder.append(line.substring(colNum));

            this.strList.add(lineNum, builder.toString());
        }
    }

    /**
     * Deletes the content from the position of the file.
     * 
     * @param newBeginPos The begin position of the content.
     * @param newEndPos The end position of the content.
     */
    private void delete(FileEditorUtil.Position newBeginPos, FileEditorUtil.Position newEndPos) {
        String beginLine = getLine(newBeginPos.getLine());
        String endLine = getLine(newEndPos.getLine());
        int lineNum = newEndPos.getLine() - newBeginPos.getLine();

        for (int i = 0; i < lineNum; i++) {
            this.strList.remove(newBeginPos.getLine());
        }

        StringBuilder builder = new StringBuilder();

        int beginCol = FileEditorUtil.getWideColPosition(beginLine, this.tabSize, newBeginPos.getCol());
        int endCol = FileEditorUtil.getWideColPosition(endLine, this.tabSize, newEndPos.getCol());

        builder.append(beginLine.substring(0, beginCol));
        builder.append(endLine.substring(endCol));

        this.strList.set(newBeginPos.getLine() - 1, builder.toString());
    }

    /**
     * Returns the indexed line from the file.
     * 
     * @param line The line we need.
     * @return The indexed line from the file.
     */
    private String getLine(int line) {
        return this.strList.get(line - 1);
    }

    /**
     * Returns the file content line list.
     * 
     * @return The file content line list.
     */
    public List<String> toList() {
        return this.strList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = this.strList.iterator();

        while (iter.hasNext()) {
            builder.append(iter.next());
            if (iter.hasNext()) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    /**
     * Creates a line list from the given file's content.
     * 
     * @param filePath The file's URI.
     * @return The created list from the given file's content.
     * @throws FileNotFoundException If the file is not found.
     * @throws IOException If there's an input problem.
     */
    public static List<String> fileIntoLines(URI filePath) throws FileNotFoundException, IOException {
        List<String> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(filePath.toURL().openStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            result.add(line);
        }

        reader.close();

        return result;
    }
}
