package codex.codex_iter.www.awol.exceptions;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException() {
        super("Cannot connect to ITER API");
    }
}
