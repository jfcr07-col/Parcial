package service;

import exceptions.InvalidSerialNumberException;
import exceptions.InvalidSeverityException;
import exceptions.InvalidSoftwareVersionException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import model.HardwareReport;
import model.Report;
import model.Severity;
import model.SoftwareReport;

/**
 * Clase que maneja la lista de reportes:
 * - Carga y guarda en disco 
 * - Consultas por id, severidad o fecha
 * - La generacion de archivos de texto (txt)
 */
public class ReportManager {
    private static final String DATA_FOLDER = "data";
    private static final String DATA_FILE = DATA_FOLDER + File.separator + "databaseReports.dat";
    private static final String REPORTS_FOLDER = "reports";

    // Lista de todos los reportes
    private List<Report> reports;

    /**
     * Se crea carpeta data si hace falta,
     * luego intenta cargar la lista desde el archivo.
     */
    public ReportManager() {
        reports = new ArrayList<>();
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            System.err.println("Error al crear carpeta data: " + e.getMessage());
        }
        loadReportsFromDisk();
    }

    /**
     * Deserializa la lista de reportes desde el archivo data o dataBase
     * Si no existe, deja la lista vacia y se le asigna "uncheked".
     */
    @SuppressWarnings("unchecked")
    private void loadReportsFromDisk() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                this.reports = (List<Report>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar reportes: " + e.getMessage());
        }
    }

    /**
     * Serializa la lista completa de reportes y la guarda en data/dataBase
     * Se llama cada vez que se agrega o genere un nuevo reporte.
     */
    private void saveReportsToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this.reports);
        } catch (IOException e) {
            System.err.println("Error al guardar reportes: " + e.getMessage());
        }
    }

    /**
     * Agrega un reporte a la lista y guarda en el disco.
     * @param r Reporte (puede ser HardwareReport o SoftwareReport)
     */
    public void addReport(Report r) {
        this.reports.add(r);
        saveReportsToDisk();
    }

    /**
     * Devuelve una lista de cadenas con severidad y fecha
     * para mostrar al usuario antes de filtrar por ID de equipo.
     */
    public List<String> listEquipmentIdAndSeverity() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return reports.stream()
                .sorted(Comparator.comparing(Report::getReportDate))
                .map(r -> r.getEquipmentId() + " (" + r.getSeverity().toString() + ", " + r.getReportDate().format(fmt) + ")")
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los niveles de severidad que ya existen en los reportes.
     * Sin duplicados, ordenados por el orden del enum (HIGH, MEDIUM, LOW).
     */
    public List<Severity> listSeverityLevelsPresent() {
        return reports.stream()
                .map(Report::getSeverity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Devuelve la fecha mas antigua y la mas reciente de los reportes.
     * Si no hay reportes, lanza una excepcion
     *
     * @return el tamaño del arreglo debe ser 2
     */
    public LocalDate[] getDateRange() {
        if (reports.isEmpty()) {
            throw new IllegalStateException("No hay reportes registrados");
        }
        LocalDate min = reports.stream()
                .map(Report::getReportDate)
                .min(LocalDate::compareTo)
                .get();
        LocalDate max = reports.stream()
                .map(Report::getReportDate)
                .max(LocalDate::compareTo)
                .get();
        return new LocalDate[]{min, max};
    }

    /**
     * Busca todos los reportes cuyo equipmentId coincide con el ingresado.
     * @param equipmentId ID del equipo a buscar
     * @return lista de reportes que coinciden
     */
    public List<Report> queryByEquipmentId(String equipmentId) {
        return reports.stream()
                .filter(r -> r.getEquipmentId().equalsIgnoreCase(equipmentId))
                .sorted(Comparator.comparing(Report::getReportDate))
                .collect(Collectors.toList());
    }

    public List<Report> queryBySeverity(Severity severity) {
        return reports.stream()
                .filter(r -> r.getSeverity() == severity)
                .sorted(Comparator.comparing(Report::getReportDate))
                .collect(Collectors.toList());
    }

    /**
     * Busca todos los reportes cuya fecha es fromDate.
     *
     * @param fromDate Fecha desde la que se busca, esta incluida
     * @return lista de reportes que coinciden
     */
    public List<Report> queryByDateFrom(LocalDate fromDate) {
        return reports.stream()
                .filter(r -> !r.getReportDate().isBefore(fromDate))
                .sorted(Comparator.comparing(Report::getReportDate))
                .collect(Collectors.toList());
    }

    /**
     * Genera un archivo .txt con todos los reportes del tipo indicado:
     * "Hardware" o "Software" 
     * El archivo se llama Reporte_<Tipo>_YYYY-MM-DD_HH-mm-ss.txt
     * y se guarda en la carpeta "reports/".
     * @param type "Hardware" o "Software"
     * @throws IOException si hay error al crear/escribir el archivo
     */
    public void generateReportFile(String type) throws IOException {
        String tipoNorm = type.trim().toLowerCase();
        boolean isHardware;
        if (tipoNorm.equals("hardware")) {
            isHardware = true;
        } else if (tipoNorm.equals("software")) {
            isHardware = false;
        } else {
            throw new IllegalArgumentException("Tipo invalido para reporte: " + type);
        }

        List<Report> filtered = reports.stream()
                .filter(r -> {
                    if (isHardware) {
                        return r instanceof HardwareReport;
                    } else {
                        return r instanceof SoftwareReport;
                    }
                })
                .sorted(Comparator.comparing(Report::getReportDate))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No hay reportes de tipo " + type);
            return;
        }

        Files.createDirectories(Paths.get(REPORTS_FOLDER));

        LocalDate today = LocalDate.now();
        String timeStamp = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
        String fileName = String.format("Reporte_%s_%s_%s.txt",
                isHardware ? "Hardware" : "Software",
                today.toString(),
                timeStamp
        );
        String fullPath = REPORTS_FOLDER + File.separator + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            for (Report r : filtered) {
                writer.write(r.toString());
                writer.newLine();
            }
        }

        System.out.println("Reporte generado en: " + fullPath);
    }

    // Metodos de validacion:

    /**
     * Valida que el numero de serie sea mayor que 0.
     *
     * @param serialNumber numero de serie a validar
     * @throws InvalidSerialNumberException si serialNumber <= 0
     */
    public void validateSerialNumber(int serialNumber) throws InvalidSerialNumberException {
        if (serialNumber <= 0) {
            throw new InvalidSerialNumberException("El numero de serie debe ser entero positivo");
        }
    }

    /**
     * Valida que la version de software siga A.B.C con numeros.
     *
     * @param version version a validar
     * @throws InvalidSoftwareVersionException si no cumple el formato
     */
    public void validateSoftwareVersion(String version) throws InvalidSoftwareVersionException {
        String regex = "^[0-9]+\\.[0-9]+\\.[0-9]+$";
        if (!Pattern.matches(regex, version.trim())) {
            throw new InvalidSoftwareVersionException("La version debe ser A.B.C con numeros");
        }
    }

    /**
     * Convierte texto a Severity o lanza excepcion si es invalido.
     *
     * @param input texto de severidad
     * @return Severity correspondiente
     * @throws InvalidSeverityException si no coincide con Alto, medio o bajo
     */
    public Severity parseSeverity(String input) throws InvalidSeverityException {
        try {
            return Severity.fromString(input);
        } catch (IllegalArgumentException ex) {
            throw new InvalidSeverityException("Severidad invalida. Use Alto/Medio/Bajo");
        }
    }

    /**
     * Convierte texto a LocalDate en formato YYYY-MM-DD.
     *
     * @param input texto de fecha
     * @return LocalDate resultante
     * @throws DateTimeParseException si no es formato valido
     */
    public LocalDate parseDate(String input) throws DateTimeParseException {
        return LocalDate.parse(input.trim());
    }
}
