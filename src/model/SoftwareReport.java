package model;

import java.time.LocalDate;

/**
 * Reporte de software, se extiende de Report.
 * Incluye sistema operativo, nombre de software y tambien la version.
 */
public class SoftwareReport extends Report {
    private static final long serialVersionUID = 1L;

    // Sistema operativo, por ejemplo windows
    private String operatingSystem;
    // Nombre del software con daños
    private String softwareName;
    // Version en el formato A.B.C
    private String version;

    /**
     * Se crea un reporte de software.
     *
     * @param equipmentId     ID del equipo
     * @param description     Descripcion del incidente
     * @param severity        Nivel de severidad
     * @param reportDate      Fecha del reporte
     * @param operatingSystem Sistema operativo del equipo
     * @param softwareName    Nombre del software
     * @param version         Version en formato A.B.C
     */
    public SoftwareReport(
            String equipmentId,
            String description,
            Severity severity,
            LocalDate reportDate,
            String operatingSystem,
            String softwareName,
            String version
    ) {
        super(equipmentId, description, severity, reportDate);
        this.operatingSystem = operatingSystem;
        this.softwareName = softwareName;
        this.version = version;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        // Todos los factores separados por guiones
        // Ejemplo: "EQ2 - Error instalando - Medio - 2025-05-18 - Windows - Office - 2.3.1"
        return equipmentId + " - "
                + description + " - "
                + severity.toString() + " - "
                + reportDate.toString() + " - "
                + operatingSystem + " - "
                + softwareName + " - "
                + version;
    }
}
