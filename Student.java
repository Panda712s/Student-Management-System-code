import java.util.Objects;

/**
 * Represents a single student record.
 */
public class Student {
    private int id;
    private String name;
    private int age;
    private String course;
    private double gpa;

    public Student(int id, String name, int age, String course, double gpa) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.gpa = gpa;
    }

    // Overloaded constructor - GPA defaults to 0.0 (Method Overloading)
    public Student(int id, String name, int age, String course) {
        this(id, name, age, course, 0.0);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    // Used for File I/O (CSV format)
    public String toFileString() {
        return id + "," + name + "," + age + "," + course + "," + gpa;
    }

    public static Student fromFileString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        int age = Integer.parseInt(parts[2].trim());
        String course = parts[3].trim();
        double gpa = Double.parseDouble(parts[4].trim());
        return new Student(id, name, age, course, gpa);
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d Name: %-20s Age: %-4d Course: %-15s GPA: %.2f",
                id, name, age, course, gpa);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
