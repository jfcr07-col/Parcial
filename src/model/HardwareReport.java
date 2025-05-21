package model;

import java.time.LocalDate;

/**
 * Reporte de hardware, se extiende de Report.
 * Incluye tipo de componente, numero de serie y si necesita reemplazo.
 */
public class HardwareReport extends Report {
    private static final long serialVersionUID = 1L;

    // Tipo de componente 
    private String componentType;
    // Numero de serie del componente, debe ser un entero positivo
    private int serialNumber;
    // Boolean por si se necesita reemplazar el componente
    private boolean needsReplacement;

    /**
     * Se crea un reporte de hardware.
     *
     * @param equipmentId      ID del equipo
     * @param description      Descripcion del incidente
     * @param severity         Nivel de severidad
     * @param reportDate       Fecha del reporte
     * @param componentType    Tipo de componente dañado
     * @param serialNumber     Numero de serie (debe ser mayor a 0
     * @param needsReplacement True si requiere reemplazo
     */
    public HardwareReport(
            String equipmentId,
            String description,
            Severity severity,
            LocalDate reportDate,
            String componentType,
            int serialNumber,
            boolean needsReplacement
    ) {
        super(equipmentId, description, severity, reportDate);
        this.componentType = componentType;
        this.serialNumber = serialNumber;
        this.needsReplacement = needsReplacement;
    }

    public String getComponentType() {
        return componentType;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public boolean isNeedsReplacement() {
        return needsReplacement;
    }

    @Override
    public String toString() {
        // Todos los factores deben ser devueltos separados por guiones
        // Ejemplo: "EQ1 - Disco dañado - Alto - 2025-05-18 - Disk - 12345 - Si"
        return equipmentId + " - "
                + description + " - "
                + severity.toString() + " - "
                + reportDate.toString() + " - "
                + componentType + " - "
                + serialNumber + " - "
                + (needsReplacement ? "Si" : "No");
    }
}
