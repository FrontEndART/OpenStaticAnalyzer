package coderepair.communication.exceptions;

public class RepairAlgIllegalArgumentException extends RepairAlgorithmRuntimeException {

    private static final long serialVersionUID = 8012079620278596805L;

    public RepairAlgIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepairAlgIllegalArgumentException(String message) {
        super(message);
    }

}
