package exceptions;

/**
 * Excepcion que se mandas si la severidad es invalida
 */
public class InvalidSeverityException extends Exception {
    /**
     * Se hace la excepcion con un mensaje de error.
     *
     * @param message Explicacion de por que la severidad es invalida
     */
    public InvalidSeverityException(String message) {
        super(message);
    }
}
