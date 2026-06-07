import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

class TodoList {
    private ArrayList<String> tasks;
    private final String filePath = "data/todo.csv";

    public TodoList() {
        this.tasks = new ArrayList<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            File file = new File(filePath);
            if (!file.exists()) return;
            Scanner fileScanner = new Scanner(file);
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine();
            }
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    int commaIndex = line.indexOf(',');
                    if (commaIndex >= 0) {
                        tasks.add(line.substring(commaIndex + 1));
                    }
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
        }
    }

    private int getLastId() {
        return tasks.size();
    }

    public void add(String task) {
        tasks.add(task);
        int newId = getLastId();
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            boolean fileIsNew = !file.exists();
            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            if (fileIsNew) {
                pw.println("id,task");
            }
            pw.println(newId + "," + task);
            pw.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void print() {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ": " + tasks.get(i));
        }
    }

    private boolean updateFile() {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            pw.println("id,task");
            for (int i = 0; i < tasks.size(); i++) {
                pw.println((i + 1) + "," + tasks.get(i));
            }
            pw.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error updating file: " + e.getMessage());
            return false;
        }
    }

    public void remove(int number) {
        tasks.remove(number - 1);
        updateFile();
    }

    public boolean checkEventString(String value) {
        if (value.length() < 3) return false;
        return value.matches("[a-zA-Z0-9 ]+");
    }
}

class UserInterface {
    private TodoList todoList;
    private Scanner scanner;

    public UserInterface(TodoList todoList, Scanner scanner) {
        this.todoList = todoList;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.print("Command: ");
            String command = scanner.nextLine();

            if (command.equals("stop")) {
                break;
            } else if (command.equals("add")) {
                System.out.print("To add: ");
                String task = scanner.nextLine();
                if (todoList.checkEventString(task)) {
                    todoList.add(task);
                } else {
                    System.out.println("Invalid task! Must be at least 3 characters, letters/numbers/spaces only.");
                }
            } else if (command.equals("list")) {
                todoList.print();
            } else if (command.equals("remove")) {
                System.out.print("Which one is removed? ");
                int number = Integer.parseInt(scanner.nextLine());
                todoList.remove(number);
            }
        }
    }
}

public class ToDoList {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TodoList todoList = new TodoList();
        UserInterface ui = new UserInterface(todoList, scanner);
        ui.start();
    }
}