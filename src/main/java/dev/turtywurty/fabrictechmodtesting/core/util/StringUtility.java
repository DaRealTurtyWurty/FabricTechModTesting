package dev.turtywurty.fabrictechmodtesting.core.util;

public final class StringUtility {
    private static final String[] QUANTIFIERS = new String[]{"k", "M", "B", "T", "Q"};

    private StringUtility() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String formatNumberWithQuantifier(long number, int decimalPlaces) {
        if (number < 1000) {
            return String.valueOf(number);
        }

        int exp = (int) (Math.log(number) / Math.log(1000));
        var formatString = String.format("%%.%df%%s", decimalPlaces);
        var result = String.format(formatString, number / Math.pow(1000, exp), QUANTIFIERS[exp - 1]);

        return result.replaceAll("\\.0+", "");
    }

    public static String formatNumberWithQuantifier(long number) {
        return formatNumberWithQuantifier(number, 1);
    }
}
