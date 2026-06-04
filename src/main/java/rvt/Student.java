package rvt;

public class Student extends Person {
    private int credits;

    public Student(String name, String address) {
        super(name, address);
        this.credits = 0;
    }

    public Student(String name, String address, int credits) {
        super(name, address);
        this.credits = credits;
    }

    public void study() {
        this.credits += 1;
    }

    public int getCredits() {
        return this.credits;
    }

    @Override
    public String toString() {
        return this.getName() + "   " + this.getAddress() + "   Study credits " + this.credits;
    }
}