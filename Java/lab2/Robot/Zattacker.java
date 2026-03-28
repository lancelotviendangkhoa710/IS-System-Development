public class Zattacker extends Robot {
    private double P;

    public Zattacker() {
        super(50,0);
        this.P = 20 + (Math.random() * 10); // Ngẫu nhiên [20, 30]
    }

    public void output() {
        System.out.println("--- Robot Zattacker ---");
        super.output();
        System.out.println("Suc manh P: " + P);
        System.out.println("Nang luong tieu thu: " + calculatingEnegry());
    }

    @Override
    public double calculatingEnegry() {
        // Công thức: M*S + P*P*S
        return M * S + P * P * S;
    }
}