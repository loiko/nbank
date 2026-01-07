package ui.utils;

import java.util.Locale;

public class Utils {
   private Utils(){}

    public static String formatMoney(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }
}
