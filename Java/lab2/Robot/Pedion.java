import java.util.Scanner;

public class Pedion extends Robot {
    private double F;

    public Pedion() {
        super();
        F = 1 + (Math.random() * 4);
    }

    public Pedion(double m, double s) {
        super(20, s);
        F = 1 + (Math.random() * 4);
    }

   public double  getF (){
        return F;
    }
    public void input() {
      super.input();
    }
    public void output(){
        System.out.println("Robot pedion \n");
        super.output();
        System.out.println("Do linh hoat"+getF());
    }

    public  double calculatingEnegry (){
            return M* S+ ( F+1)* S/2 ;
    }

}
