package point;
import java.util.Scanner;

public class Point {
    private double x;
    private double y;

    private static Scanner in = new Scanner(System.in);

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    // Getter & Setter
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double value) { x = value; }
    public void setY(double value) { y = value; }

    public void Input() {
        System.out.print("Nhap vao truc x: ");
        this.x = in.nextDouble();
        System.out.print("Nhap vao truc y: ");
        this.y = in.nextDouble(); 
    }

    public double calculatingDistance(Point b) {
 
        return Math.sqrt(Math.pow(this.x - b.x, 2) + Math.pow(this.y - b.y, 2));
    }

    public static void main(String[] args) {
  
        Point a = new Point();
        a.Input();
        
        Point b = new Point();
        b.Input();
        
        System.out.println("Khoang cach: " + a.calculatingDistance(b));
    }
}