public class Carrier extends Robot {
    private double E;

    public Carrier() {
        super(30,0);
        this.E = 50 + (Math.random() * 50); // Ngẫu nhiên [50, 100]
    }

    public void output() {
        System.out.println("--- Robot Carrier ---");
        super.output();
        System.out.println("Kho nang luong E: " + E);
        System.out.println("Nang luong tieu thu: " + calculatingEnegry());
    }

    @Override
    public double calculatingEnegry() {
        // Công thức: M*S + 4*E*S
        return M * S + 4 * E * S;
    }
}