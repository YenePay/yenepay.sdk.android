package examples.mob.yenepay.com.checkoutcounter;

public class Util {
    public static String getFormattedAmt(double amt){
        return String.format("%1$,.2f", amt);
    }
}
