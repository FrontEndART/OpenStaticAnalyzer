package coderepair.generator.support.iterators;

import java.net.URI;

import coderepair.generator.fileeditor.FileEditorUtil.Position;
import coderepair.generator.fileeditor.FilePositionUpdater;

public class BackLetterIterator extends AbstractLetterIterator {

    public BackLetterIterator(URI srcDir, String path, Position end, int tabSize) {
        super(srcDir, path, new Position(1, 1), end, tabSize, SliceMinusOnePositionSetter.INSTANCE);
    }

    private Position getRelativePosition() {
        String slice = getSlice();
        int line = FilePositionUpdater.getNumberOfCharacters(slice.substring(0, this.position + 1), '\n');
        int lastNLPos = slice.lastIndexOf('\n', this.position);
        int col = FilePositionUpdater.staticGetStringLength(slice.substring(lastNLPos, this.position), getTabSize());

        return new Position(line, col);
    }

    @Override
    public Position getPosition() {
        Position tmp = getRelativePosition();
        return Position.addPositions(tmp, new Position(1, tmp.getCol() + 1));
    }

    @Override
    public boolean hasNext() {
        return this.position >= 0;
    }

    @Override
    protected int nextPosition() {
        return this.position--;
    }

    @Override
    public Position getEndPosition() {
        return getPosition(-1);
    }

    public enum SliceMinusOnePositionSetter implements PositionSetter {
        INSTANCE;

        @Override
        public int getPosition(String slice) {
            return slice.length() - 1;
        }

    }

}
