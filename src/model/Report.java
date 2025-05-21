package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Clase base para un reporte (hardware o software).
 * Define los campos que van a tener obligatoriamente todos los reportes
 */
public abstract class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    // Id del equipo
    protected String equipmentId;
    // Se describe el incidente
    protected String description;
    // Nivel de la severidad del daño 
    protected Severity severity;
    // Fecha del reporte
    protected LocalDate reportDate;

    /**
     * Se crea un reporte con sus datos basicos.
     *
     * @param equipmentId ID del equipo
     * @param description  Descripcion del incidente
     * @param severity     Nivel de severidad
     * @param reportDate   Fecha del reporte
     */
    public Report(String equipmentId, String description, Severity severity, LocalDate reportDate) {
        this.equipmentId = equipmentId;
        this.description = description;
        this.severity = severity;
        this.reportDate = reportDate;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    /**
     * Cada subclase debe devolver todos sus campos separados por guiones.
     */
    @Override
    public abstract String toString();
}
