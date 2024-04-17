package fr.norsys.docmanagementapi.exception;

public class DocAlreadyExistException extends ResourceAlreadyExistException {
    public DocAlreadyExistException(String message) {
        super(message);
    }
}
