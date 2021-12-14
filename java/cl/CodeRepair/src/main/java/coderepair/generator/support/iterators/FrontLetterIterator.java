package coderepair.generator.support.iterators;

import java.net.URI;

import coderepair.generator.fileeditor.FileEditorUtil.Position;
import coderepair.generator.fileeditor.FilePositionUpdater;

public class FrontLetterIterator extends AbstractLetterIterator {

    private Position startPos;

    public FrontLetterIterator(URI srcDir, String path, Position start, int tabSize) {
        super(srcDir, path, start, null, tabSize, NullPositionSetter.INSTANCE);

        this.startPos = start;
    }

    private Position getRelativePosition() {
        String slice = getSlice();
        int line = FilePositionUpdater.getNumberOfCharacters(slice.substring(0, this.position), '\n');
        int lastNLPos = slice.lastIndexOf('\n', this.position - 1) + 1;
        int col = FilePositionUpdater.staticGetStringLength(slice.substring(lastNLPos, this.position), getTabSize()) + (line > 0 ? 1 : 0);

        return new Position(line, col);
    }

    @Override
    public Position getPosition() {
        return Position.addPositions(this.startPos, getRelativePosition());
    }

    @Override
    public boolean hasNext() {
        return this.position < getSliceLength();
    }

    @Override
    protected int nextPosition() {
        return this.position++;
    }

    @Override
    public Position getBeginPosition() {
        return getPosition(1);
    }

    public enum NullPositionSetter implements PositionSetter {
        INSTANCE;

        @Override
        public int getPosition(String slice) {
            return 0;
        }

    }

}
