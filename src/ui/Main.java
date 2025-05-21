package ui;

import exceptions.InvalidSerialNumberException;
import exceptions.InvalidSeverityException;
import exceptions.InvalidSoftwareVersionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import model.Report;
import model.Severity;
import service.ReportManager;

/**
 * Programa principal con menu en consola.
 * Permite crear reportes, realizar consultas y generar archivos de texto.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ReportManager manager = new ReportManager();

    public static void main(String[] args) {
        System.out.println("=== Sistema de Gestion de Reportes de Equipos ===");
        boolean exit = false;
        while (!exit) {
            printMenu();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    createHardwareReport();
                    break;
                case "2":
                    createSoftwareReport();
                    break;
                case "3":
                    queryByEquipmentId();
                    break;
                case "4":
                    queryBySeverity();
                    break;
                case "5":
                    queryByDateFrom();
                    break;
                case "6":
                    generateReportFile();
                    break;
                case "7":
                    exit = true;
                    System.out.println("Saliendo..."); 
                    break;
                default:
                    System.out.println("Opcion invalida. Ingresa 1-7.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nSelecciona una opcion:");
        System.out.println("1. Crear reporte de Hardware");
        System.out.println("2. Crear reporte de Software");
        System.out.println("3. Consultar reportes por ID de equipo");
        System.out.println("4. Consultar reportes por nivel de severidad");
        System.out.println("5. Consultar reportes desde una fecha");
        System.out.println("6. Generar archivo de informe (txt)");
        System.out.println("7. Salir");
        System.out.print("Opcion: ");
    }

    private static void createHardwareReport() {
        try {
            System.out.print("Ingresa ID del equipo: ");
            String eqId = scanner.nextLine().trim();

            System.out.print("Descripcion incidente (hardware): ");
            String description = scanner.nextLine().trim();

            System.out.print("Severidad (Alto/Medio/Bajo): ");
            String sevInput = scanner.nextLine().trim();
            Severity severity = manager.parseSeverity(sevInput);

            System.out.print("Fecha reporte (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            LocalDate date = manager.parseDate(dateInput);

            System.out.print("Tipo de componente (ej. Motherboard, Disk): ");
            String componentType = scanner.nextLine().trim();

            System.out.print("Numero de serie (entero positivo): ");
            int serialNumber;
            try {
                serialNumber = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException nfe) {
                System.out.println("Numero de serie invalido. Debe ser entero.");
                return;
            }
            manager.validateSerialNumber(serialNumber);

            System.out.print("Necesita reemplazo? (Si/No): ");
            String needsRepInput = scanner.nextLine().trim().toLowerCase();
            boolean needsReplacement = needsRepInput.equals("si") || needsRepInput.equals("s");

            Report hwReport = new model.HardwareReport(
                    eqId,
                    description,
                    severity,
                    date,
                    componentType,
                    serialNumber,
                    needsReplacement
            );
            manager.addReport(hwReport);
            System.out.println("Reporte de hardware guardado.");

        } catch (InvalidSeverityException | DateTimeParseException | InvalidSerialNumberException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error inesperado: " + ex.getMessage());
        }
    }

    private static void createSoftwareReport() {
        try {
            System.out.print("Ingresa ID del equipo: ");
            String eqId = scanner.nextLine().trim();

            System.out.print("Descripcion incidente (software): ");
            String description = scanner.nextLine().trim();

            System.out.print("Severidad (Alto/Medio/Bajo): ");
            String sevInput = scanner.nextLine().trim();
            Severity severity = manager.parseSeverity(sevInput);

            System.out.print("Fecha reporte (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            LocalDate date = manager.parseDate(dateInput);

            System.out.print("Sistema operativo (ej. Windows 10): ");
            String os = scanner.nextLine().trim();

            System.out.print("Nombre del software: ");
            String softwareName = scanner.nextLine().trim();

            System.out.print("Version (A.B.C): ");
            String version = scanner.nextLine().trim();
            manager.validateSoftwareVersion(version);

            Report swReport = new model.SoftwareReport(
                    eqId,
                    description,
                    severity,
                    date,
                    os,
                    softwareName,
                    version
            );
            manager.addReport(swReport);
            System.out.println("Reporte de software guardado.");

        } catch (InvalidSeverityException | DateTimeParseException | InvalidSoftwareVersionException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error inesperado: " + ex.getMessage());
        }
    }

    private static void queryByEquipmentId() {
        List<String> resumen = manager.listEquipmentIdAndSeverity();
        if (resumen.isEmpty()) {
            System.out.println("No hay reportes.");
            return;
        }
        System.out.println("Reportes (ID - Severidad - Fecha):");
        resumen.forEach(System.out::println);

        System.out.print("Ingresa ID del equipo a consultar: ");
        String eqId = scanner.nextLine().trim();
        List<Report> encontrados = manager.queryByEquipmentId(eqId);
        if (encontrados.isEmpty()) {
            System.out.println("No se encontraron reportes para " + eqId);
        } else {
            System.out.println("Resultados para " + eqId + ":");
            encontrados.forEach(r -> System.out.println("  - " + r.toString()));
        }
    }

    private static void queryBySeverity() {
        List<Severity> niveles = manager.listSeverityLevelsPresent();
        if (niveles.isEmpty()) {
            System.out.println("No hay reportes.");
            return;
        }
        System.out.println("Severidades registradas:");
        niveles.forEach(s -> System.out.println("  - " + s.toString()));

        System.out.print("Ingresa severidad a consultar (Alto/Medio/Bajo): ");
        String sevInput = scanner.nextLine().trim();
        try {
            Severity sev = manager.parseSeverity(sevInput);
            List<Report> encontrados = manager.queryBySeverity(sev);
            if (encontrados.isEmpty()) {
                System.out.println("No hay reportes con severidad " + sev.toString());
            } else {
                System.out.println("Resultados para severidad " + sev.toString() + ":");
                encontrados.forEach(r -> System.out.println("  - " + r.toString()));
            }
        } catch (InvalidSeverityException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void queryByDateFrom() {
        try {
            LocalDate[] rango = manager.getDateRange();
            System.out.println("Fecha mas antigua: " + rango[0].toString());
            System.out.println("Fecha mas reciente: " + rango[1].toString());
            System.out.print("Ingresa fecha desde (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            LocalDate fromDate = manager.parseDate(input);
            List<Report> encontrados = manager.queryByDateFrom(fromDate);
            if (encontrados.isEmpty()) {
                System.out.println("No hay reportes desde " + fromDate.toString());
            } else {
                System.out.println("Resultados desde " + fromDate.toString() + ":");
                encontrados.forEach(r -> System.out.println("  - " + r.toString()));
            }
        } catch (IllegalStateException ise) {
            System.out.println("Error: " + ise.getMessage());
        } catch (DateTimeParseException dtpe) {
            System.out.println("Fecha invalida. Use YYYY-MM-DD.");
        }
    }

    private static void generateReportFile() {
        System.out.print("Tipo de informe a generar (Hardware/Software): ");
        String tipo = scanner.nextLine().trim();
        try {
            manager.generateReportFile(tipo);
        } catch (IllegalArgumentException iae) {
            System.out.println("Error: " + iae.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error al crear archivo: " + ioe.getMessage());
        }
    }
}
