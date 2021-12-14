package coderepair.communication.base;


/**
 * Points out the position of a problem in the source code.
 */
public class ProblemPosition {

    final private String path;
    final private Integer startLine;
    final private Integer startCol;
    final private Integer endLine;
    final private Integer endCol;

    public ProblemPosition(String path, Integer startLine, Integer startCol, Integer endLine, Integer endCol) {
        super();
        this.path = path;
        this.startLine = startLine;
        this.startCol = startCol;
        this.endLine = endLine;
        this.endCol = endCol;
    }

    public String getPath() {
        return this.path;
    }

    public Integer getStartLine() {
        return this.startLine;
    }

    public Integer getStartCol() {
        return this.startCol;
    }

    public Integer getEndLine() {
        return this.endLine;
    }

    public Integer getEndCol() {
        return this.endCol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.endCol == null) ? 0 : this.endCol.hashCode());
        result = prime * result + ((this.endLine == null) ? 0 : this.endLine.hashCode());
        result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
        result = prime * result + ((this.startCol == null) ? 0 : this.startCol.hashCode());
        result = prime * result + ((this.startLine == null) ? 0 : this.startLine.hashCode());
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
        ProblemPosition other = (ProblemPosition) obj;
        if (this.endCol == null) {
            if (other.endCol != null) {
                return false;
            }
        } else if (!this.endCol.equals(other.endCol)) {
            return false;
        }
        if (this.endLine == null) {
            if (other.endLine != null) {
                return false;
            }
        } else if (!this.endLine.equals(other.endLine)) {
            return false;
        }
        if (this.path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!this.path.equals(other.path)) {
            return false;
        }
        if (this.startCol == null) {
            if (other.startCol != null) {
                return false;
            }
        } else if (!this.startCol.equals(other.startCol)) {
            return false;
        }
        if (this.startLine == null ) {
            if (other.startLine != null) {
                return false;
            }
        } else if (!this.startLine.equals(other.startLine)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProblemPosition [path=" + this.path + ", startLine=" + this.startLine + ", startCol="
                + this.startCol + ", endLine=" + this.endLine + ", endCol=" + this.endCol + "]";
    }

}
