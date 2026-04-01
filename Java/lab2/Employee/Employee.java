package org.example;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Employee extends Person {
    private float salary;

    public Employee() {
        super();
        salary = 0;
    }

    public Employee(String name, int age, float salary) {
        super(name, age);
        this.salary = salary;
    }

    public float getSalary() { return salary; }

    public void setSalary(float salary) { this.salary = salary; };

    public void show()
    {
        System.out.println("Tên nhân viên: " + getName());
        System.out.println("Tuổi nhân viên: " + getAge());
        DecimalFormat df = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.getDefault()));
        System.out.println("Lương nhân viên: " + df.format(salary) + " vnd");
    }

    public void addSalary()
    {
        salary *= 1.1f;
    }

    public void addSalary(float tiLe)
    {
        salary *= (1 + tiLe/100);
    }

}
