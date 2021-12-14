package coderepair.communication.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a problem which has to be repaired.
 */
public class ProblemToRepair {

    private List<ProblemPosition> positions;

    public ProblemToRepair() {
        super();
        this.positions = new ArrayList<ProblemPosition>();
    }

    public ProblemToRepair(List<ProblemPosition> positions) {
        super();
        this.positions = positions;
    }

    public List<ProblemPosition> getPositions() {
        return this.positions;
    }

    public void setPositions(List<ProblemPosition> positions) {
        this.positions = positions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.positions == null) ? 0 : this.positions.hashCode());
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
        ProblemToRepair other = (ProblemToRepair) obj;
        if (this.positions == null) {
            if (other.positions != null) {
                return false;
            }
        } else if (!this.positions.equals(other.positions)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProblemToRepair [positions=" + this.positions + "]";
    }

}
