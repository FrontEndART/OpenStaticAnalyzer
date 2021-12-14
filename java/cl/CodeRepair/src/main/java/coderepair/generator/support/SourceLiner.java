package coderepair.generator.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import columbus.java.asg.base.Base;

/**
 * The source-liner class.
 */
public class SourceLiner {

    protected List<String> stringList;
    protected StringBuilder builder;
    protected boolean actLineEmpty;

    public SourceLiner() {
        this.stringList = new LinkedList<>();
        clearBuilder();
    }

    public void appendHeader() {
    }

    public void appendFooter() {
    }

    public String getOutputExtension() {
        return ".java";
    }

    public void append(String str) {
        if (str.contains("\n")) {
            throw new IllegalArgumentException(
                    "The parameter should not contain new line.");
        }

        this.actLineEmpty = false;
        this.builder.append(str);
    }

    public void newLine() {
        this.stringList.add(this.builder.toString());
        clearBuilder();
    }

    public void clear() {
        this.stringList.clear();
        clearBuilder();
    }

    private void clearBuilder() {
        this.actLineEmpty = true;
        this.builder = new StringBuilder();
    }

    public void appendIdTitledStr(String str, Base... base) {
        append(str);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        Iterator<String> iter = this.stringList.iterator();
        while (iter.hasNext()) {
            result.append(iter.next());
            result.append('\n');
        }
        result.append(this.builder.toString());

        return result.toString();
    }
}
