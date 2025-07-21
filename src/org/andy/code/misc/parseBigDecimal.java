package org.andy.code.misc;

import java.math.BigDecimal;

public class parseBigDecimal {

    private parseBigDecimal() {
        // Utility class – private constructor to prevent instantiation
    }

    /**
     * Konvertiert ein BigDecimal in einen String
     * Ersetzt Komma durch Punkt.
     */
    public static String fromBD(BigDecimal input) {
        return input.toString().replace(",", ".");
    }
    
    /**
     * Konvertiert einen String zu BigDecimal.
     * Behandelt führende/trailing Whitespaces und ersetzt Komma durch Punkt.
     */
    public static BigDecimal fromString(String input) {
        if (input == null || input.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(input.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültiger numerischer Wert: " + input, e);
        }
    }

    /**
     * Konvertiert ein Objekt (z. B. aus einem Array) zu BigDecimal.
     */
    public static BigDecimal fromObject(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        return fromString(obj.toString());
    }

    /**
     * Liest ein Element aus einem 2D-Array vom Typ String[][] und konvertiert es zu BigDecimal.
     */
    public static BigDecimal fromArray(String[][] array, int row, int col) {
        if (array == null || row < 0 || col < 0 || row >= array.length || col >= array[row].length) {
            throw new IndexOutOfBoundsException("Ungültiger Array-Zugriff bei [" + row + "][" + col + "]");
        }
        return fromString(array[row][col]);
    }

    /**
     * Liest ein Element aus einem 2D-Array vom Typ Object[][] und konvertiert es zu BigDecimal.
     */
    public static BigDecimal fromArray(Object[][] array, int row, int col) {
        if (array == null || row < 0 || col < 0 || row >= array.length || col >= array[row].length) {
            throw new IndexOutOfBoundsException("Ungültiger Array-Zugriff bei [" + row + "][" + col + "]");
        }
        return fromObject(array[row][col]);
    }
}

