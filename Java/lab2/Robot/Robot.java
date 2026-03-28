import java.util.Scanner;

public class Robot {
    protected double M ;
    protected double S ;
    protected Scanner in = new Scanner(System.in);
    public Robot (){
        M=0;
        S=0;
    }

    public Robot (double m , double s){
        M=m;
        S=s;
    }

    protected double getM ( ){
        return M;
    }
    protected double getS ( ){
        return S;
    }

    protected void setM(double value){
        M=value;
    }
    protected void setS(double value ){
        S= value;
    }
    protected double calculatingEnegry (){
        return 0;
    }

    protected  void input(){
        System.out.println("Nhap trong luong M  \n");
        double m = in.nextDouble();
        setM(m);

        System.out.println("Nhap quan duong di S  \n");
        double s = in.nextDouble();
        setS(s);
    }
    protected  void output (){
        System.out.println("Thong tin Robot  \n");
    }




}
