package lab02.quanlynhanvien;

public class Employee extends Person {
    private float salary;
    
    public Employee(String name, int age, float salary) {
        super(name, age);
        this.salary = salary;
    }
    
    public float getSalary() {
        return salary;
    }
    
    public void setSalary(float salary) {
        this.salary = salary;
    }
    
    @Override 
    public void show() {
        System.out.printf("Tên nhân viên: %s, Tuổi: %d, Lương: %,.0f\n",
        getName(), getAge(), salary);
    }
    
    public void addSalary() {
        salary *= 1.1f;
    }
    
    public void addSalary(float amount) {
        salary += amount;
    }
}