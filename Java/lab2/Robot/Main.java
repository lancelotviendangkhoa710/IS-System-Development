import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static  void main (String [] agrs ) {
        Scanner in = new Scanner(System.in);
        ArrayList<Robot> doanRobot = new ArrayList<>();
        //  Nhập số lượng từng loại
        System.out.println("Nhap so luong Pedion (A): ");
        int A = in.nextInt();
        System.out.println("Nhap so luong Zattacker (B): ");
        int B = in.nextInt();
        System.out.println("Nhap so luong Carrier (C): ");
        int C = in.nextInt();


        double quangDuong = 10;

        for (int i = 0; i < A; i++) {
            Pedion p = new Pedion();
            p.setS(quangDuong);
            p.setM(20);
            doanRobot.add(p);
        }
        for (int i = 0; i < B; i++) {
            Zattacker z = new Zattacker();
            z.setS(quangDuong);
            doanRobot.add(z);
        }
        for (int i = 0; i < C; i++) {
            Carrier c = new Carrier();
            c.setS(quangDuong);
            doanRobot.add(c);
        }


        double sumP = 0, sumZ = 0, sumC = 0;
        System.out.println("\n======= THONG TIN DOAN ROBOT =======");
        for (Robot r : doanRobot) {
            double energy = r.calculatingEnegry();
            if (r instanceof Pedion) sumP += energy;
            else if (r instanceof Zattacker) sumZ += energy;
            else if (r instanceof Carrier) sumC += energy;
        }
        System.out.println("\n======= KET QUA THONG KE =======");
        System.out.println("Tong nang luong Pedion: " + sumP);
        System.out.println("Tong nang luong Zattacker: " + sumZ);
        System.out.println("Tong nang luong Carrier: " + sumC);

        double maxEnergy = Math.max(sumP, Math.max(sumZ, sumC));

        System.out.print("Loai robot tieu thu nang luong nhieu nhat la: ");
        if (maxEnergy == sumP) System.out.println("Pedion");
        else if (maxEnergy == sumZ) System.out.println("Zattacker");
        else System.out.println("Carrier");

    }
}
