package org.andy.code.misc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public final class ArithmeticHelper {

    private ArithmeticHelper() {}
    
    private static final Pattern EU_PATTERN = Pattern.compile("^[+-]?(?:\\d{1,3}(?:\\.\\d{3})+|\\d+)(?:,\\d+)?$");
    private static final Pattern US_PATTERN = Pattern.compile("^[+-]?(?:\\d{1,3}(?:,\\d{3})+|\\d+)(?:\\.\\d+)?$");

    //###################################################################################################################################################
    // String -> Zahl
    //###################################################################################################################################################
    
    public enum LocaleFormat {
        EU, US
    }

    public static int parseStringToIntSafe(String s) {
        if (s == null) return 0;
        s = s.trim();
        // Nur optionale Vorzeichen + Ziffern erlauben
        if (!s.matches("^[+-]?\\d+$")) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) { // Überlauf o. Ä.
            return 0;
        }
    }

    public static long parseStringToLongSafe(String s) {
        if (s == null) return 0L;
        s = s.trim();
        if (!s.matches("^[+-]?\\d+$")) return 0L;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static BigDecimal parseStringToBigDecimalSafe(String s, LocaleFormat format) {
        if (s == null) return BD.ZERO;
        s = s.trim();
        if (s.isEmpty()) return BD.ZERO;

        try {
            switch (format) {
                case EU:
                    if (!EU_PATTERN.matcher(s).matches()) return BD.ZERO;
                    s = s.replace(".", "").replace(',', '.');
                    break;
                case US:
                    if (!US_PATTERN.matcher(s).matches()) return BD.ZERO;
                    s = s.replace(",", "");
                    break;
                default:
                    return BD.ZERO;
            }
            return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BD.ZERO;
        }
    }

    //###################################################################################################################################################
    // Zahl -> String
    //###################################################################################################################################################

    public static String parseIntToStringSafe(int i) {
        return String.valueOf(i);
    }

    public static String parseLongToStringSafe(long l) {
        return String.valueOf(l);
    }

    public static String parseBigDecimalToStringSafe(BigDecimal bd) {
        if (bd == null) return "0.00";
        return bd.toPlainString();
    }
    
}
