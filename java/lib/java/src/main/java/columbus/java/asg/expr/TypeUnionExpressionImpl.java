/*
 *  This file is part of OpenStaticAnalyzer.
 *
 *  Copyright (c) 2004-2018 Department of Software Engineering - University of Szeged
 *
 *  Licensed under Version 1.2 of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence in the LICENSE file or at:
 *
 *  https://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 */

package columbus.java.asg.expr;

import columbus.IO;
import columbus.java.asg.*;
import columbus.java.asg.base.BaseImpl;
import columbus.java.asg.base.Base;
import columbus.java.asg.type.Type;
import columbus.java.asg.base.Comment;
import columbus.java.asg.enums.*;
import columbus.java.asg.visitors.Visitor;
import columbus.logger.LoggerHandler;

/**
 * Implementation class for the {@link columbus.java.asg.expr.TypeUnionExpression TypeUnionExpression} node.
 * <p><b>WARNING: For internal use only.</b></p>
 */
public class TypeUnionExpressionImpl extends BaseImpl implements TypeUnionExpression {

	@SuppressWarnings("unused")
	private static final LoggerHandler logger = new LoggerHandler(TypeUnionExpressionImpl.class, columbus.java.asg.Constant.LoggerPropertyFile);
	protected EdgeList<Comment> _comments;

	protected Object position;

	protected boolean isCompilerGenerated;

	protected boolean isToolGenerated;

	protected int _type;

	protected EdgeList<TypeExpression> _hasAlternatives;

	public TypeUnionExpressionImpl(int id, Factory factory) {
		super(id, factory);
		position = new Range(factory.getStringTable());
	}

	@Override
	public NodeKind getNodeKind() {
		return NodeKind.ndkTypeUnionExpression;
	}

	@Override
	public Range getPosition() {
		return (Range)position;
	}

	@Override
	public void setPosition(Range _position) {
		if (factory.getStringTable() == _position.getStringTable())
			position = _position;
		else
			position = new Range(factory.getStringTable(), _position);
	}

	@Override
	public boolean getIsCompilerGenerated() {
		return isCompilerGenerated;
	}

	@Override
	public boolean getIsToolGenerated() {
		return isToolGenerated;
	}

	@Override
	public void setIsCompilerGenerated(boolean _isCompilerGenerated) {
		isCompilerGenerated = _isCompilerGenerated;
	}

	@Override
	public void setIsToolGenerated(boolean _isToolGenerated) {
		isToolGenerated = _isToolGenerated;
	}

	@Override
	public EdgeIterator<Comment> getCommentsIterator() {
		if (_comments == null)
			return EdgeList.<Comment>emptyList().iterator();
		else
			return _comments.iterator();
	}

	@Override
	public boolean getCommentsIsEmpty() {
		if (_comments == null)
			return true;
		else
			return _comments.isEmpty();
	}

	@Override
	public int getCommentsSize() {
		if (_comments == null)
			return 0;
		else
			return _comments.size();
	}

	@Override
	public void addComments(int _id) {
		if (!factory.getExist(_id))
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));

		Base _node = factory.getRef(_id);
		if (Common.getIsBaseClassKind(_node.getNodeKind(), NodeKind.ndkComment)) {
			if (_comments == null)
				_comments = new EdgeList<Comment>(factory);
			_comments.add(_id);
		} else {
			throw new JavaException(logger.formatMessage("ex.java.Node.Invalid","NodeKind", _node.getNodeKind() ));
		}
	}

	@Override
	public void addComments(Comment _node) {
		if (_comments == null)
			_comments = new EdgeList<Comment>(factory);
		_comments.add(_node);
	}

	@Override
	public void addComments(Comment _node, int index) {
		if (_comments == null)
			_comments = new EdgeList<Comment>(factory);
		_comments.add(_node, index);
	}

	@Override
	public void setComments(Comment _node, int index) {
		if (_comments == null)
			_comments = new EdgeList<Comment>(factory);
		_comments.set(_node, index);
	}

	@Override
	public void removeComments(Comment _node) {
		if (_node == null)
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));

		_comments.remove(_node);
	}

	@Override
	public void removeComments(int _id) {
		int tmp=_comments.remove(_id);
		if (tmp==0)
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));
		else removeParentEdge(tmp);
	}

	@Override
	public Type getType() {
		if (_type == 0)
			return null;
		if (factory.getIsFiltered(_type))
			return null;
		return (Type)factory.getRef(_type);
	}

	@Override
	public void setType(int _id) {
		if (!factory.getExist(_id))
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));

		Base _node = factory.getRef(_id);
		if (Common.getIsBaseClassKind(_node.getNodeKind(), NodeKind.ndkType)) {
			_type = _id;
		} else {
			throw new JavaException(logger.formatMessage("ex.java.Node.Invalid","NodeKind", _node.getNodeKind() ));
		}
	}

	@Override
	public void setType(Type _node) {
		_type = _node.getId();
	}

	@Override
	public void removeType() {
		_type = 0;
	}

	@Override
	public EdgeIterator<TypeExpression> getAlternativesIterator() {
		if (_hasAlternatives == null)
			return EdgeList.<TypeExpression>emptyList().iterator();
		else
			return _hasAlternatives.iterator();
	}

	@Override
	public boolean getAlternativesIsEmpty() {
		if (_hasAlternatives == null)
			return true;
		else
			return _hasAlternatives.isEmpty();
	}

	@Override
	public int getAlternativesSize() {
		if (_hasAlternatives == null)
			return 0;
		else
			return _hasAlternatives.size();
	}

	@Override
	public void addAlternatives(int _id) {
		if (!factory.getExist(_id))
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));

		Base _node = factory.getRef(_id);
		if (Common.getIsBaseClassKind(_node.getNodeKind(), NodeKind.ndkTypeExpression)) {
			if (_hasAlternatives == null)
				_hasAlternatives = new EdgeList<TypeExpression>(factory);
			_hasAlternatives.add(_id);
		} else {
			throw new JavaException(logger.formatMessage("ex.java.Node.Invalid","NodeKind", _node.getNodeKind() ));
		}
		setParentEdge(_id);
	}

	@Override
	public void addAlternatives(TypeExpression _node) {
		if (_hasAlternatives == null)
			_hasAlternatives = new EdgeList<TypeExpression>(factory);
		_hasAlternatives.add(_node);
		setParentEdge(_node);
	}

	@Override
	public void addAlternatives(TypeExpression _node, int index) {
		if (_hasAlternatives == null)
			_hasAlternatives = new EdgeList<TypeExpression>(factory);
		_hasAlternatives.add(_node, index);
		setParentEdge(_node);
	}

	@Override
	public void setAlternatives(TypeExpression _node, int index) {
		if (_hasAlternatives == null)
			_hasAlternatives = new EdgeList<TypeExpression>(factory);
		_hasAlternatives.set(_node, index);
		setParentEdge(_node);
	}

	@Override
	public void removeAlternatives(TypeExpression _node) {
		if (_node == null)
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));

		_hasAlternatives.remove(_node);

		removeParentEdge(_node);
	}

	@Override
	public void removeAlternatives(int _id) {
		int tmp=_hasAlternatives.remove(_id);
		if (tmp==0)
			throw new JavaException(logger.formatMessage("ex.java.Node.No_end_point"));
		else removeParentEdge(tmp);
	}


	// ---------- Accept methods for Visitor ----------

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this, true);
	}

	@Override
	public void acceptEnd(Visitor visitor) {
		visitor.visitEnd(this, true);
	}


	// ---------- Save ----------

	@Override
	public void save(IO io) {
		io.writeInt4(id);
		io.writeUShort2(getNodeKind().ordinal());


		if (_comments != null) {
			EdgeIterator<Comment> it = getCommentsIterator();
			while (it.hasNext()) {
				io.writeInt4(it.next().getId());
			}
		}
		io.writeInt4(0);

		io.writeInt4(((Range)position).getPathKey());
		io.writeInt4(((Range)position).getLine());
		io.writeInt4(((Range)position).getCol());
		io.writeInt4(((Range)position).getEndLine());
		io.writeInt4(((Range)position).getEndCol());
		io.writeInt4(((Range)position).getWideLine());
		io.writeInt4(((Range)position).getWideCol());
		io.writeInt4(((Range)position).getWideEndLine());
		io.writeInt4(((Range)position).getWideEndCol());
		{
			byte boolValues = 0;
			boolValues <<= 1;
			if (isCompilerGenerated) 
				boolValues |= 1;
			boolValues <<= 1;
			if (isToolGenerated) 
				boolValues |= 1;
			io.writeByte1(boolValues);
		}


		io.writeInt4(!factory.getIsFiltered(_type) ? _type : 0);

		if (_hasAlternatives != null) {
			EdgeIterator<TypeExpression> it = getAlternativesIterator();
			while (it.hasNext()) {
				io.writeInt4(it.next().getId());
			}
		}
		io.writeInt4(0);
	}


	// ---------- Load ----------

	@Override
	public void load(IO io) {
		int _id;



		_id = io.readInt4();
		if (_id != 0) {
			_comments = new EdgeList<Comment>(factory);
			while (_id != 0) {
				_comments.add(_id);
				_id = io.readInt4();
			}
		}

		((Range)position).setPathKey(io.readInt4());
		((Range)position).setLine(io.readInt4());
		((Range)position).setCol(io.readInt4());
		((Range)position).setEndLine(io.readInt4());
		((Range)position).setEndCol(io.readInt4());
		((Range)position).setWideLine(io.readInt4());
		((Range)position).setWideCol(io.readInt4());
		((Range)position).setWideEndLine(io.readInt4());
		((Range)position).setWideEndCol(io.readInt4());
		{
			byte boolValues = io.readByte1();
			isToolGenerated = (boolValues & 1) != 0;
			boolValues >>>= 1;
			isCompilerGenerated = (boolValues & 1) != 0;
			boolValues >>>= 1;
		}


		_type = io.readInt4();


		_id = io.readInt4();
		if (_id != 0) {
			_hasAlternatives = new EdgeList<TypeExpression>(factory);
			while (_id != 0) {
				_hasAlternatives.add(_id);
				setParentEdge(_id);
				_id = io.readInt4();
			}
		}
	}

}
