package coderepair.generator.support.iterators;

import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;

import coderepair.generator.fileeditor.FileEditorUtil;
import coderepair.generator.fileeditor.FileEditorUtil.FilePart;
import coderepair.generator.fileeditor.FileEditorUtil.Position;
import com.google.gdata.util.common.base.CharEscapers;

public abstract class AbstractLetterIterator implements Iterator<Character> {

    private String slice;
    protected int position;
    private int tabSize;

    public AbstractLetterIterator(URI srcDir, String path, Position beginPos, Position endPos, int tabSize, PositionSetter setter) {
        super();

        URI resolved = srcDir.resolve(CharEscapers.uriPathEscaper().escape(path.replace("\\", "/")));

        this.slice = FileEditorUtil.getFileSlice(resolved, new FilePart(path, beginPos, endPos), tabSize);
        this.position = setter.getPosition(this.slice);
        this.tabSize = tabSize;
    }

    protected String getSlice() {
        return this.slice;
    }

    protected int getTabSize() {
        return this.tabSize;
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return this.slice.charAt(nextPosition());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("This operation is not implemented at all.");
    }

    protected Position getPosition(int back) {
        int tmp = this.position;

        this.position -= back;

        Position back_position = getPosition();

        this.position = tmp;

        return back_position;
    }

    protected int getSliceLength() {
        return this.slice.length();
    }

    public Position getBeginPosition() {
        return getPosition();
    }

    public Position getEndPosition() {
        return getPosition();
    }

    protected abstract int nextPosition();

    public abstract Position getPosition();

    public static interface PositionSetter {

        public int getPosition(String slice);
    }
}
