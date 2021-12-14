package coderepair.generator.fileeditor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Helper class for FileEditor and FilePositionUpdater.
 */
public class FileEditorUtil {

    /**
     * The position storer class.
     */
    public static class Position {

        final private int line;
        final private int col;

        /**
         * The copy constructor.
         * 
         * @param pos The copied position.
         */
        public Position(Position pos) {
            this(pos.getLine(), pos.getCol());
        }

        /**
         * The another constructor.
         * 
         * @param line The line position.
         * @param col The column position.
         */
        public Position(int line, int col) {
            super();
            this.line = line;
            this.col = col;
        }

        /**
         * Returns the line position.
         * 
         * @return The line position.
         */
        public int getLine() {
            return this.line;
        }

        /**
         * Returns the column position.
         * 
         * @return The column position.
         */
        public int getCol() {
            return this.col;
        }

        /**
         * Returns true if the actual position is greater than the given position.
         * 
         * @param pos The another position.
         * @return True if the actual position is greater than the given position.
         */
        public boolean isGreaterThan(Position pos) {
            return this.line > pos.getLine() || (this.line == pos.getLine() && this.col > pos.getCol());
        }

        /**
         * Returns true if the actual position is lower than the given position.
         * 
         * @param pos The another position.
         * @return True if the actual position is lower than the given position.
         */
        public boolean isLowerThan(Position pos) {
            return this.line < pos.getLine() || (this.line == pos.getLine() && this.col < pos.getCol());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.col;
            result = prime * result + this.line;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Position other = (Position) obj;
            if (this.col != other.col) {
                return false;
            }
            if (this.line != other.line) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "[" + this.line + ", " + this.col + "]";
        }

        public static Position addPositions(Position pos1, Position pos2) {
            int newLine = pos1.getLine() + pos2.getLine();
            int newCol = pos2.getLine() > 0 ? pos2.getCol() : pos1.getCol() + pos2.getCol();

            return new Position(newLine, newCol);
        }
    }

    /**
     * The class for storing FilePart.
     */
    public static class FilePart {

        final private String filename;
        final private Position beginPos;
        final private Position endPos;

        /**
         * The constructor.
         * 
         * @param filename The name of the file.
         * @param beginPos The begin position.
         * @param endPos The end position.
         */
        public FilePart(String filename, Position beginPos, Position endPos) {
            this.filename = filename;
            this.beginPos = beginPos;
            this.endPos = endPos;
        }

        /**
         * The constructor.
         * 
         * @param filename The name of the file.
         * @param line The begin line of the FilePart.
         * @param col The begin column of the FilePart.
         * @param endline The end line of the FilePart.
         * @param endcol The end column of the FilePart.
         */
        public FilePart(String filename, int line, int col, int endline, int endcol) {
            this(filename, new Position(line, col), new Position(endline, endcol));
        }

        /**
         * Returns the filename.
         * 
         * @return The filename.
         */
        public String getFilename() {
            return this.filename;
        }

        /**
         * Returns the begin position.
         * 
         * @return The begin position.
         */
        public Position getBeginPos() {
            return this.beginPos;
        }

        /**
         * Returns the end position.
         * 
         * @return The end position.
         */
        public Position getEndPos() {
            return this.endPos;
        }

        /**
         * Appends two FilePart into one.
         * 
         * @param part The one part.
         * @param appended The another part.
         * @return The appended FilePart.
         */
        public static FilePart append2FilePart(FilePart part, Position appended) {
            Position begin = part.getBeginPos();
            Position end = part.getEndPos();

            if (begin.isGreaterThan(appended)) {
                begin = appended;
            } else if (end.isLowerThan(appended)) {
                end = appended;
            }

            return new FilePart(part.getFilename(), begin, end);
        }
    }

    /**
     * Returns the content of the FilePart.
     * 
     * @param path The file string.
     * @param position The position.
     * @param tabsize The tabsize.
     * @return The content of the FilePart.
     */
    public static String getFileSlice(URI path, FilePart position, int tabsize) {
        BufferedReader reader = null;
        Position begin = position.getBeginPos();
        Position end = position.getEndPos();

        try {
            reader = new BufferedReader(new InputStreamReader(path.toURL().openStream()));

            String line = null;
            int linenum = 0;
            while (linenum < begin.getLine() && (line = reader.readLine()) != null) {
                linenum++;
            }

            if (line == null) {
                throw new IllegalArgumentException("The position.getWideLine is bigger than the file length.");
            }

            if (end != null && begin.getLine() == end.getLine()) {
                int getWideColPos = getWideColPosition(line, tabsize, begin.getCol());
                int getWideEndColPos = getWideColPosition(line, tabsize, end.getCol());

                return line.substring(getWideColPos, getWideEndColPos);
            } else {
                StringBuilder result = new StringBuilder();

                int getWideColPos = getWideColPosition(line, tabsize, begin.getCol());
                result.append(line.substring(getWideColPos));
                while ((end == null || linenum < end.getLine()) && (line = reader.readLine()) != null) {
                    result.append('\n');
                    if (end != null && linenum + 1 == end.getLine()) {
                        int getWideEndColPos = getWideColPosition(line, tabsize, end.getCol());
                        result.append(line.substring(0, getWideEndColPos));
                    } else {
                        result.append(line);
                    }
                    linenum++;
                }

                if (end != null && linenum < end.getLine()) {
                    throw new IllegalArgumentException("The file is smaller than the end position line number.");
                }

                return result.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Returns the wide column size of the position.
     * 
     * @param line The target line.
     * @param tabsize The size of the tab.
     * @param col The target column.
     * @return The wide column size of the position.
     */
    public static int getWideColPosition(String line, int tabsize, int col) {
        if (col == 0) {
            return col;
        }

        if (tabsize == 1) {
            return col - 1;
        }

        int offset = 0;
        int realCol = col - 1;
        int index = line.indexOf('\t');
        while (index != -1 && index < realCol) {
//            realCol -= tabsize - 1;
            int minus = (offset + index) % tabsize + 1;
            int aktOffset = tabsize - minus;
            offset += aktOffset;
            realCol -= aktOffset;
            index = line.indexOf('\t', index + 1);
        }

        return realCol;
    }
}
