package coderepair.generator.fileeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for the file position update.
 */
public class FilePositionUpdater {

    /**
     * Class for position offset.
     */
    protected static class PositionOffset extends FileEditorUtil.Position {

        final private int lineOffset;
        final private int colOffset;

        /**
         * The constructor for PositionOffset.
         * 
         * @param pos The position.
         * @param lineOff The line offset.
         * @param offs The column offset.
         */
        public PositionOffset(FileEditorUtil.Position pos, int lineOff, int offs) {
            super(pos);
            this.lineOffset = lineOff;
            this.colOffset = offs;
        }

        /**
         * Returns the line offset.
         * 
         * @return The line offset.
         */
        public int getLineOffset() {
            return this.lineOffset;
        }

        /**
         * Returns the column offset.
         * 
         * @return The column offset.
         */
        public int getColOffset() {
            return this.colOffset;
        }
    }

    /**
     * Stores the list of the offsets.
     */
    protected static class OffsetList {

        public static final boolean DEFAULT_ISAFTER = true;

        private List<PositionOffset> lst;

        /**
         * Default constructor.
         */
        public OffsetList() {
            super();
            this.lst = new ArrayList<PositionOffset>();
        }

        /**
         * Adds an offset element to the list.
         * 
         * @param elem The given element.
         */
        public void add(PositionOffset elem) {
            this.lst.add(elem);
        }

        /**
         * Returns the size of the list.
         * 
         * @return The size of the list.
         */
        public int size() {
            return this.lst.size();
        }

        /**
         * Returns the iterator for the list.
         * 
         * @return The iterator for the list.
         */
        public Iterator<PositionOffset> iterator() {
            return this.lst.iterator();
        }

        /**
         * Returns the new position for the given position.
         * 
         * @param oldPos The old position.
         * @return The new position for the given position.
         */
        public FileEditorUtil.Position getNewPosition(FileEditorUtil.Position oldPos) {
            return getNewPosition(oldPos, DEFAULT_ISAFTER);
        }

        // isAfter csak akkor kell, ha ""-t cseréltünk ki valamire..
        /**
         * Returns the new position for the given position.
         * 
         * @param oldPos The old position.
         * @param isAfter If true, the position adds after the previous inserted.
         * @return The new position for the given position.
         */
        public FileEditorUtil.Position getNewPosition(FileEditorUtil.Position oldPos, boolean isAfter) {
            int lineOffs = oldPos.getLine();
            int colOffs = oldPos.getCol();

            // TODO: isAfter-t beépíteni newPos-ba!!
            Iterator<PositionOffset> iter = iterator();
            while (iter.hasNext()) {
                PositionOffset offs = iter.next();
                if (offs.getLine() < lineOffs ||
                        offs.getLine() == lineOffs && offs.getCol() < colOffs ||
                        (isAfter && offs.getLine() == lineOffs && offs.getCol() == colOffs)) {

                    if (offs.getLine() == lineOffs && offs.getCol() <= colOffs) {
                        colOffs += offs.getColOffset();
                    }

                    lineOffs += offs.getLineOffset();
                }
            }

            return new FileEditorUtil.Position(lineOffs, colOffs);
        }
    }

    private OffsetList offList;
    private int tabSize;

    /**
     * Constructor.
     * 
     * @param tabsize The tab size.
     */
    public FilePositionUpdater(int tabsize) {
        super();
        this.offList = new OffsetList();
        this.tabSize = tabsize;
    }

    /**
     * Returns the new position from the old one.
     * 
     * @param pos The old position.
     * @return The new position from the old one.
     */
    public FileEditorUtil.Position getNewPosition(FileEditorUtil.Position pos) {
        return this.offList.getNewPosition(pos);
    }

    /**
     * Returns the new position from the old one.
     * 
     * @param pos The old position.
     * @param isAfter If true, the position adds after the previous inserted.
     * @return The new position from the old one.
     */
    public FileEditorUtil.Position getNewPosition(FileEditorUtil.Position pos, boolean isAfter) {
        return this.offList.getNewPosition(pos, isAfter);
    }

    /**
     * Adds a new offsets.
     * 
     * @param old The filepart.
     * @param newStr The added String.
     */
    public void addNewOffsets(FileEditorUtil.FilePart old, String newStr) {
        addNewOffsets(old, newStr, OffsetList.DEFAULT_ISAFTER);
    }

    /**
     * Adds a new offsets.
     * 
     * @param old The filepart.
     * @param newStr The added String.
     * @param isAfter If true, the position adds after the previous inserted.
     */
    public void addNewOffsets(FileEditorUtil.FilePart old, String newStr, boolean isAfter) {
        FileEditorUtil.Position pos = getNewPosition(old.getBeginPos(), isAfter);
        FileEditorUtil.Position endPos = getNewPosition(old.getEndPos(), isAfter);

        final int newLineHeight = getNumberOfRows(newStr);
        final int newEndColPos = getColHeight(newStr, pos.getCol() - 1) + 1;// FIXME -1+1 combination!

        final int lineHeight = endPos.getLine() - pos.getLine() + 1;
        final int colHeight = endPos.getCol();

        final int lineOffset = newLineHeight - lineHeight;
        final int colOffset = newEndColPos - colHeight;

        this.offList.add(new PositionOffset(endPos, lineOffset, colOffset));
    }

    /**
     * Returns the column height.
     * 
     * @param newStr The actual line.
     * @param col The column.
     * @return The column height.
     */
    private int getColHeight(String newStr, int col) {
        int lastNL = newStr.lastIndexOf('\n');
        if (lastNL == -1) {
            return getStringLength(newStr, col);
        } else {
            return getStringLength(newStr.substring(lastNL + 1));
        }
    }

    /**
     * Returns the String length.
     * 
     * @param newStr The akt string.
     * @param col The column.
     * @return The String length.
     */
    private int getStringLength(String newStr, int col) {
        return staticGetStringLength(newStr, this.tabSize, col);
    }

    /**
     * Returns the String length.
     * 
     * @param newStr The akt string.
     * @return The String length.
     */
    private int getStringLength(String newStr) {
        return staticGetStringLength(newStr, this.tabSize);
    }

    /**
     * Returns the line height.
     * 
     * @param newStr The actual string.
     * @return Returns the number of lines of the given parameter string
     */
    private int getNumberOfRows(String newStr) {
        return getNumberOfCharacters(newStr, '\n') + 1;
    }

    /**
     * Returns the number of the given character.
     * 
     * @param newStr The text.
     * @param character The character.
     * @return The number of the given character.
     */
    public static int getNumberOfCharacters(String newStr, char character) {
        int result = 0;
        int index = -1;
        while ((index = newStr.indexOf(character, index + 1)) != -1) {
            result++;
        }

        return result;
    }

    /**
     * Returns the String's length.
     * 
     * @param newStr The string.
     * @param tabSize The size of the tab.
     * @return The String's length.
     */
    public static int staticGetStringLength(String newStr, int tabSize) {
        return staticGetStringLength(newStr, tabSize, 0);
    }

    /**
     * Returns the String's length.
     * 
     * @param newStr The string.
     * @param tabSize The size of the tab.
     * @param startCol The start column.
     * @return The String's length.
     */
    public static int staticGetStringLength(String newStr, int tabSize, int startCol) {
        int result = startCol;
        int length = newStr.length();

        for (int index = 0; index < length; ++index) {
            char aktchr = newStr.charAt(index);
            if (aktchr == '\t') {
                result = getNewTabSize(result, tabSize);
            } else {
                result += 1;
            }
        }

        return result;
    }

    /**
     * Returns the new tab size.
     * 
     * @param actSize The actual tab size.
     * @param tabSize The size of the tab.
     * @return The new tab size.
     */
    private static int getNewTabSize(int actSize, int tabSize) {
        int result = actSize + tabSize;

        result -= result % tabSize;

        return result;
    }
}
