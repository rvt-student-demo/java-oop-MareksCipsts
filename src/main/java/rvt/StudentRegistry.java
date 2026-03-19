package rvt;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class StudentRegistry {
    private static List<Student> students;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        students = FileHandler.loadStudents();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Studentu Reģistrācijas Sistēma ---");
            System.out.println("1. register - Reģistrēt jaunu studentu");
            System.out.println("2. show     - Rādīt visus studentus");
            System.out.println("3. remove   - Dzēst studentu");
            System.out.println("4. edit     - Rediģēt studentu");
            System.out.println("5. search   - Meklēt studentu (Papildfunkcija)");
            System.out.println("6. exit     - Iziet");
            System.out.print("Izvēlieties darbību: ");
            
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "1": case "register": registerStudent(); break;
                case "2": case "show": TablePrinter.printStudents(students); break;
                case "3": case "remove": removeStudent(); break;
                case "4": case "edit": editStudent(); break;
                case "5": case "search": searchStudent(); break;
                case "6": case "exit": 
                    running = false; 
                    FileHandler.saveStudents(students);
                    System.out.println("Dati saglabāti. Uz redzēšanos!");
                    break;
                default: System.out.println("Nezināma komanda. Lūdzu mēģiniet vēlreiz.");
            }
        }
    }

    private static void registerStudent() {
        try {
            System.out.print("Ievadiet vārdu: ");
            String name = scanner.nextLine();
            Validator.validateName(name);

            System.out.print("Ievadiet uzvārdu: ");
            String surname = scanner.nextLine();
            Validator.validateName(surname);

            System.out.print("Ievadiet e-pastu: ");
            String email = scanner.nextLine();
            Validator.validateEmail(email);
            if (students.stream().anyMatch(s -> s.getEmail().equals(email))) {
                throw new IllegalArgumentException("Kļūda: Šāds e-pasts jau ir reģistrēts.");
            }

            System.out.print("Ievadiet personas kodu (XXXXXX-XXXXX): ");
            String pk = scanner.nextLine();
            Validator.validatePersonalCode(pk);
            if (students.stream().anyMatch(s -> s.getPersonalCode().equals(pk))) {
                throw new IllegalArgumentException("Kļūda: Šāds personas kods jau eksistē.");
            }

            students.add(new Student(name, surname, email, pk, LocalDateTime.now()));
            FileHandler.saveStudents(students);
            System.out.println("Students veiksmīgi reģistrēts!");

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeStudent() {
        System.out.print("Ievadiet dzēšamā studenta personas kodu: ");
        String pk = scanner.nextLine();
        boolean removed = students.removeIf(s -> s.getPersonalCode().equals(pk));
        if (removed) {
            FileHandler.saveStudents(students);
            System.out.println("Students dzēsts veiksmīgi.");
        } else {
            System.out.println("Students ar šādu personas kodu netika atrasts.");
        }
    }

    private static void editStudent() {
        System.out.print("Ievadiet rediģējamā studenta personas kodu: ");
        String pk = scanner.nextLine();
        Student toEdit = students.stream().filter(s -> s.getPersonalCode().equals(pk)).findFirst().orElse(null);

        if (toEdit == null) {
            System.out.println("Students nav atrasts.");
            return;
        }

        try {
            System.out.println("Atstājiet tukšu, ja nevēlaties mainīt vērtību.");
            System.out.print("Jauns e-pasts (" + toEdit.getEmail() + "): ");
            String newEmail = scanner.nextLine();
            if (!newEmail.isEmpty()) {
                Validator.validateEmail(newEmail);
                // Izdzēšam veco un pievienojam atjaunināto
                students.remove(toEdit);
                students.add(new Student(toEdit.getName(), toEdit.getSurname(), newEmail, toEdit.getPersonalCode(), toEdit.getRawDate()));
                FileHandler.saveStudents(students);
                System.out.println("Dati atjaunoti!");
            } else {
                System.out.println("Izmaiņas netika veiktas.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchStudent() {
        System.out.print("Ievadiet meklējamo vārdu vai uzvārdu: ");
        String query = scanner.nextLine().toLowerCase();
        List<Student> results = students.stream()
                .filter(s -> s.getName().toLowerCase().contains(query) || s.getSurname().toLowerCase().contains(query))
                .toList();
        
        if(results.isEmpty()) {
            System.out.println("Neviens students netika atrasts.");
        } else {
            System.out.println("Meklēšanas rezultāti:");
            TablePrinter.printStudents(results);
        }
    }
}

// Datu modelis
class Student {
    private String name;
    private String surname;
    private String email;
    private String personalCode;
    private LocalDateTime registrationDate;

    public Student(String name, String surname, String email, String personalCode, LocalDateTime registrationDate) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.personalCode = personalCode;
        this.registrationDate = registrationDate;
    }

    public String toCSV() {
        return String.join(",", name, surname, email, personalCode, registrationDate.toString());
    }

    public static Student fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        return new Student(parts[0], parts[1], parts[2], parts[3], LocalDateTime.parse(parts[4]));
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getPersonalCode() { return personalCode; }
    public LocalDateTime getRawDate() { return registrationDate; }
    
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return registrationDate.format(formatter);
    }
}

// Ievaddatu validācija (RegEx)
class Validator {
    private static final String NAME_REGEX = "^[A-Za-zĀ-ž]{3,}$";
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PK_REGEX = "^\\d{6}-\\d{5}$";

    public static void validateName(String name) throws IllegalArgumentException {
        if (!Pattern.matches(NAME_REGEX, name)) {
            throw new IllegalArgumentException("Kļūda: Vārdam/Uzvārdam jāsatur tikai burti un jābūt vismaz 3 simbolus garam.");
        }
    }

    public static void validateEmail(String email) throws IllegalArgumentException {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("Kļūda: Nekorekts e-pasta formāts.");
        }
    }

    public static void validatePersonalCode(String pk) throws IllegalArgumentException {
        if (!Pattern.matches(PK_REGEX, pk)) {
            throw new IllegalArgumentException("Kļūda: Nekorekts personas kods (jābūt formātā XXXXXX-XXXXX).");
        }
    }
}

// Darbs ar failiem
class FileHandler {
    private static final String FILE_NAME = "students.csv";

    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return students;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    students.add(Student.fromCSV(line));
                }
            }
        } catch (IOException | java.time.format.DateTimeParseException e) {
            System.out.println("Kļūda lasot failu. Iespējams bojāti dati: " + e.getMessage());
        }
        return students;
    }

    public static void saveStudents(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                bw.write(s.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Kļūda saglabājot failu: " + e.getMessage());
        }
    }
}

// Tabulas izvadīšana konsolē
class TablePrinter {
    public static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Sistēmā nav reģistrētu studentu.");
            return;
        }

        String format = "| %-12s | %-12s | %-25s | %-13s | %-16s |%n";
        String separator = "+--------------+--------------+---------------------------+---------------+------------------+";

        System.out.println(separator);
        System.out.printf(format, "Vārds", "Uzvārds", "E-pasts", "Personas kods", "Reģ. datums");
        System.out.println(separator);

        for (Student s : students) {
            System.out.printf(format, s.getName(), s.getSurname(), s.getEmail(), s.getPersonalCode(), s.getFormattedDate());
        }
        System.out.println(separator);
    }
}