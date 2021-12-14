package coderepair.generator.transformation.exceptions;

import coderepair.communication.exceptions.RepairAlgorithmRuntimeException;

public class IncomparableNodePositionsException extends RepairAlgorithmRuntimeException {

    private static final long serialVersionUID = 348447122890617535L;

    public IncomparableNodePositionsException() {
        super();
    }

    public IncomparableNodePositionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncomparableNodePositionsException(String message) {
        super(message);
    }

    public IncomparableNodePositionsException(Throwable cause) {
        super(cause);
    }

}
