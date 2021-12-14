package coderepair.generator.transformation;

import columbus.IO;
import columbus.java.asg.Factory;
import columbus.java.asg.Range;
import columbus.java.asg.base.Base;
import columbus.java.asg.base.PositionedWithoutComment;
import columbus.java.asg.enums.NodeKind;
import columbus.java.asg.visitors.Visitor;

public class TestPositioned implements PositionedWithoutComment {

    private Range position;

    public TestPositioned(int sl, int sc, int el, int ec) {
        this.position = new Range(null, 0, sl, sc, el, ec, sl, sc, el, ec);
    }

    @Override
    public Range getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Range value) {
        this.position = value;
    }

    @Override
    public NodeKind getNodeKind() {
        return null;
    }

    @Override
    public Factory getFactory() {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
    }

    @Override
    public void acceptEnd(Visitor visitor) {
    }

    @Override
    public void save(IO io) {
    }

    @Override
    public void load(IO io) {
    }

    @Override
    public Base getParent() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }

}
