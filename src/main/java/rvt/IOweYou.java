package rvt;
import java.util.HashMap;

public class IOweYou {
    HashMap<String, Double> debtor;
    public IOweYou() {
        debtor = new HashMap<>();
    }

    public void setSum(String toWhom, double amount) {
        this.debtor.put(toWhom, amount);
    }
    public double howMuchDoIOWeTo(String toWhom) {
        return
        debtor.get(toWhom);
    }
    public static void main(String[] args) {
        IOweYou mattsIOweYou = new IOweYou();
        mattsIOweYou.setSum("Arthur", 51.5);
        mattsIOweYou.setSum("Michael", 30);

        System.out.println("Matt owes Arthur: " + mattsIOweYou.howMuchDoIOWeTo("Arthur"));
        System.out.println(mattsIOweYou.howMuchDoIOWeTo("Michael"));
        
    }
}