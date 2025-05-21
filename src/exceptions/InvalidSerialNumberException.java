package exceptions;

/**
 * Excepcion que se lanza cuando el numero de serie para un reporte de hardware no es un entero positivo.
 */
public class InvalidSerialNumberException extends Exception {
    /**
     * Crea la excepcion con un mensaje de error.
     *
     * @param message Explicacion de por que el numero de serie es invalido
     */
    public InvalidSerialNumberException(String message) {
        super(message);
    }
}
