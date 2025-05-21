package exceptions;

/**
 * Excepcion que se lanza cuando la version de software
 * no sigue el formato A.B.C donde A, B y C son numeros.
 */
public class InvalidSoftwareVersionException extends Exception {
    /**
     * Crea la excepcion con un mensaje de error.
     *
     * @param message Explicacion de por que la version es invalida
     */
    public InvalidSoftwareVersionException(String message) {
        super(message);
    }
}
