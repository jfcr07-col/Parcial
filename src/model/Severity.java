package model;

import java.io.Serializable;

// Representa cual es el nivel de severidad generada en cada reporte 
public enum Severity implements Serializable {
    HIGH,
    MEDIUM,
    LOW;

    /**
     * Convierte una cadena a Severity, sin distinguir ni mayusculas ni minusculas.
     *
     * @param value Texto como alto, bajo o medio
     * @return Severity correspondiente
     * @throws IllegalArgumentException si no coincide con ninguno
     */
    public static Severity fromString(String value) throws IllegalArgumentException {
        String normalized = value.trim().toUpperCase();
        switch (normalized) {
            case "ALTO":
            case "HIGH":
                return HIGH;
            case "MEDIO":
            case "MEDIUM":
                return MEDIUM;
            case "BAJO":
            case "LOW":
                return LOW;
            default:
                throw new IllegalArgumentException("Severidad invalida: " + value);
        }
    }

    @Override
    public String toString() {
        // Para mostrar en la salida
        switch (this) {
            case HIGH:
                return "Alto";
            case MEDIUM:
                return "Medio";
            case LOW:
                return "Bajo";
            default:
                return this.name();
        }
    }
}
