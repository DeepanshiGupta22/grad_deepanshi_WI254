import java.util.*;
import java.util.stream.Collectors;

class Employee {
    private String name;
    private int age;
    private String gender;
    private double salary;
    private String desig;
    private String dept;

    public Employee(String name, int age, String gender, double salary, String desig, String dept) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.salary = salary;
        this.desig = desig;
        this.dept = dept;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getDesignation() { return desig; }
    public String getDepartment() { return dept; }

    @Override
    public String toString() {
        return String.format("%-6s | Age: %d | %-6s | Sal: %.1f | %-10s | Dept: %s", 
                name, age, gender, salary, desig, dept);
    }
}

public class EmployeeStreamAnalysis {

    public static void main(String[] args) {
        
        List<Employee> employees = Arrays.asList(
            new Employee("Emp1",  28, "Female", 50000,"Developer", "IT"),
            new Employee("Emp2",  35, "Male",   80000, "Manager",   "IT"),
            new Employee("Emp3",  42, "Male",   120000,"Director",  "Admin"),
            new Employee("Emp4",  25, "Female", 45000, "Intern",    "HR"),
            new Employee("Emp5",  30, "Female", 60000, "Analyst",   "Finance"),
            new Employee("Emp6",  50, "Male",   95000, "Senior Dev","IT"),
            new Employee("Emp7",  27, "Female", 52000, "Developer", "IT"),
            new Employee("Emp8",  45, "Male",   85000, "Manager",   "Sales"),
            new Employee("Emp9",  29, "Female", 58000, "Analyst",   "Finance"),
            new Employee("Emp10", 32, "Male",   70000, "Lead",      "IT"),
            new Employee("Emp11", 24, "Male",   40000, "Intern",    "Sales"),
            new Employee("Emp12", 38, "Female", 92000, "Manager",   "HR"),
            new Employee("Emp13", 31, "Male",   62000, "Developer", "IT"),
            new Employee("Emp14", 26, "Female", 48000, "Associate", "Marketing"),
            new Employee("Emp15", 44, "Male",   110000,"Director",  "Marketing"),
            new Employee("Emp16", 55, "Male",   105000,"Consultant","IT"),
            new Employee("Emp17", 23, "Female", 42000, "Intern",    "Finance"),
            new Employee("Emp18", 34, "Female", 75000, "Lead",      "HR"),
            new Employee("Emp19", 29, "Male",   55000, "Developer", "IT"),
            new Employee("Emp20", 40, "Female", 88000, "Manager",   "Finance"),
            new Employee("Emp21", 33, "Female", 67000, "Specialist","Marketing"),
            new Employee("Emp22", 48, "Male",   98000, "Senior Dev","IT"),
            new Employee("Emp23", 27, "Female", 53000, "Developer", "Sales"),
            new Employee("Emp24", 36, "Male",   78000, "Manager",   "Ops"),
            new Employee("Emp25", 22, "Female", 38000, "Intern",    "Ops")
        );

        System.out.println("--- Employee Analysis Report ---");

        // Highest salary 
        Optional<Employee> highestPaid = employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
        
        System.out.println("\n1. Highest Salary Employee:");
        highestPaid.ifPresent(System.out::println);

        // male and female employees
        Map<String, Long> genderCount = employees.stream()
                .collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
        //groupingby takes more time, use partitioningby for 2 categories

        System.out.println("\n2. Gender Count:");
        System.out.println(genderCount);

        // Dept-wise total expense
        Map<String, Double> deptExpense = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, 
                         Collectors.summingDouble(Employee::getSalary)));
        
        System.out.println("\n3. Total Expense by Department:");
        deptExpense.forEach((dept, total) -> System.out.printf("   %-10s: %.2f%n", dept, total));

        // 5 senior employees by age
        System.out.println("\n4. Top 5 Senior Employees (by Age):");
        employees.stream()
                .sorted(Comparator.comparingInt(Employee::getAge).reversed()) 
                .limit(5)
                .forEach(System.out::println);

        // Names of all managers
        System.out.println("\n5. List of Managers:");
        employees.stream()
                .filter(e -> "Manager".equalsIgnoreCase(e.getDesignation()))
                .map(Employee::getName)
                .forEach(System.out::println);

        // Total number of employees
        long totalCount = employees.stream().count();//not optimal
        //could have done employees.size() as well
        System.out.println("\n6. Total Number of Employees: " + totalCount);

        // Salary hike for non-managers
        System.out.println("\n7. Hiking Salary (Non-Managers +20%)...");
        
        employees.stream()
                .filter(e -> !"Manager".equalsIgnoreCase(e.getDesignation()))
                .forEach(e -> {
                    double oldSal = e.getSalary();
                    e.setSalary(oldSal * 1.20);
                });
        
        System.out.println("   [Verification] Emp1 (Dev) New Salary: " + employees.get(0).getSalary()); 
        System.out.println("   [Verification] Emp2 (Mgr) New Salary: " + employees.get(1).getSalary());
    }
}