package com.voidaspect.monval;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * This utility converts various data types to and from the monetary amount,
 * which is represented as a 64-bit integer value.
 *
 * @author miwagorbi@gmail.com
 */
public final class MonetaryAmount {

    private MonetaryAmount() {
    }

    /**
     * Expected formats: {@code { xxx,xxx.xx | xxx xxx.xx | -xxxxxx.xx | xxxxxx | x.x } }
     * where x is an arabic digit character.
     * <br>A dot (.) character is considered a separator between integer and decimal parts.
     * <br>Minus (-) sign at the start of the string makes the amount negative
     * <br>Commas (,) and whitespaces are skipped
     * <br>Currency codes or symbols which can be present after the sign or at the start are skipped
     *
     * @param value string representation of a monetary amount
     * @return parsed monetary amount
     */
    public static long parse(CharSequence value) {
        int size = value.length();
        if (size == 0) throw new NumberFormatException("Can't parse monetary amount, string is empty");
        int i = 0;
        boolean negative = false;
        long integerPart = 0;
        //skipping non-digit characters, such as $ or EUR
        for (; i < size; i++) {
            char c = value.charAt(i);
            // parsing sign
            if (c == '-') {
                if (negative) throw new NumberFormatException("Only one minus sign is allowed, string: " + value);
                negative = true;
                continue;
            }
            if (c == '.')
                throw new NumberFormatException("Decimal point found before the any digits, string: " + value);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                integerPart = digit;
                break;
            }
        }
        if (i == size) throw new NumberFormatException("Can't parse monetary amount, string contains no digits");
        // parsing integer part
        for (i++; i < size; i++) {
            char c = value.charAt(i);
            if (c == '.') break;
            if (c == ' ' || c == ',') continue;
            integerPart = integerPart * 10 + digit(c);
        }
        // parsing decimal part
        int decimalPart = 0;
        int decimalDigits = size - i - 1;
        switch (Math.min(decimalDigits, 2)) { // monetary values only have 2 decimal digits
            case 2:
                decimalPart = digit(value.charAt(i + 2));
            case 1:
                decimalPart += digit(value.charAt(i + 1)) * 10;
        }
        // calculating amount, applying - sign if negative
        long amount = integerPart * 100 + decimalPart;
        if (negative) {
            amount = -amount;
        }
        return amount;
    }

    private static int digit(char c) {
        int digit = Character.digit(c, 10);
        if (digit == -1) throw new NumberFormatException(c + " is not a valid digit");
        return digit;
    }

    public static String toString(long amount) {
        StringBuilder sb = new StringBuilder();
        if (amount < 0) {
            sb.append('-');
            amount = -amount;
        }
        sb.append(amount / 100).append('.');
        long decimalPart = amount % 100;
        if (decimalPart == 0) {
            sb.append("00");
        } else {
            if (decimalPart < 10) {
                sb.append('0');
            }
            sb.append(decimalPart);
        }
        return sb.toString();
    }

    public static String toString(long amount, Currency currency) {
        return currency + " " + toString(amount);
    }

    public static String toStringWithCurrencySymbol(long amount, Currency currency) {
        return currency.getSymbol() + " " + toString(amount);
    }

    public static BigDecimal toBigDecimal(long amount) {
        return BigDecimal.valueOf(amount).movePointLeft(2);
    }

    public static double toDouble(long amount) {
        return ((double) amount) / 100;
    }

    public static long from(double amount) {
        return (long) (amount * 100);
    }

    public static long from(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.DOWN).unscaledValue().longValue();
    }

    public static long from(BigInteger amount) {
        return amount.longValue() * 100;
    }

    public static long from(long amount) {
        return amount * 100;
    }

    public static long from(int amount) {
        return amount * 100L;
    }

}
