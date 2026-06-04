public class Person {
    String name;
    String address;

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }
    public String getName() {
        return this.name;
    }
    public String getAddress() {
        return this.address;
    }
}

public class Student extends Person {
    private Int credits;

    public Student(String name, String address) {
        super(name, address);
        this.credits = 0;
    }
    public Student(String name, String address, Int credits) {
        super(name, address);
        this.credits = credits;
    }
    public void study() {
        this.credits += 1;
    }
    public Int getCredits() {
        return this.credits;
    }
    @Override
    public String toString() {
        return this.getName() + "   " + this.getAddress() + "   Study credits " + this.credits;
    }
}
public class Teacher extends Person {
    private Int salary;
    public Teacher(String name, String address, Int salary) {
        super(name, address);
        this.salary = salary;
    }
    @Override
    public String toString() {
        return this.getName() + "   " + this.getAddress() + "   Salary " + this.salary;
    }
}

public class Main {
    public static void printPersons(ArrayList<Person> persons) {
        for (Person person : persons) {
            System.out.println(person);
        }
    }

    public static void main(String[] args) {
        ArrayList<Person> persons = new ArrayList<Person>();
        persons.add(new Teacher("Ada Lovelace", "24 Maddox St. London W1S 2QN", 1200));
        persons.add(new Student("Ollie", "6381 Hollywood Blvd. Los Angeles 90028"));

        printPersons(persons);
    }
}
