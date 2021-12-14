package coderepair.communication.exceptions;

public class RepairAlgorithmRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4658640541834665526L;

    public RepairAlgorithmRuntimeException() {
        super();
    }

    public RepairAlgorithmRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepairAlgorithmRuntimeException(String message) {
        super(message);
    }

    public RepairAlgorithmRuntimeException(Throwable cause) {
        super(cause);
    }

}
