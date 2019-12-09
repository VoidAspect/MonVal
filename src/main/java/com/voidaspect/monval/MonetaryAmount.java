package com.voidaspect.monval;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

import static java.lang.Math.max;
import static java.lang.Math.min;

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
        int i;
        char current;
        boolean negative = false;
        long integerPart = 0;
        //skipping non-digit characters, such as $ or EUR
        for (i = 0; i < size; i++) {
            current = value.charAt(i);
            int digit = digit(current);
            if (digit != -1) {
                integerPart = digit;
                break;
            } else if (current == '-') { // parsing sign
                if (negative) throw new NumberFormatException("Only one minus sign is allowed, string: " + value);
                negative = true;
            } else if (current == '.') {
                throw new NumberFormatException("Decimal point found before the any digits, string: " + value);
            }
        }
        if (i == size) {
            throw new NumberFormatException("Can't parse monetary amount, string '" + value + "' contains no digits");
        }
        // parsing integer part
        for (i++; i < size; i++) {
            current = value.charAt(i);
            if (current == '.') break;
            int digit = digit(current);
            if (digit == -1) {
                if (current != ' ' && current != ',') return (negative ? -integerPart : integerPart) * 100;
            } else {
                integerPart = integerPart * 10 + digit;
            }
        }
        // parsing decimal part
        int decimalPart = 0;
        int decimalDigits = size - i - 1;
        switch (min(decimalDigits, 2)) { // monetary values only have 2 decimal digits
            case 2:
                current = value.charAt(i + 2);
                decimalPart = max(digit(current), 0);
            case 1:
                current = value.charAt(i + 1);
                decimalPart += max(digit(current), 0) * 10;
        }
        // calculating amount
        long amount = integerPart * 100 + decimalPart;
        return negative ? -amount : amount;
    }

    public static String toString(long amount) {
        StringBuilder sb = new StringBuilder();
        if (amount < 0) {
            sb.append('-');
            amount = -amount;
        }
        sb.append(amount / 100).append('.');
        int decimalPart = (int) (amount % 100);
        sb.append(digit(decimalPart / 10)).append(digit(decimalPart % 10));
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
        return amount.movePointRight(2).longValue();
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

    private static char digit(int digit) {
        return (char) ('0' + digit);
    }

    private static int digit(char c) {
        int delta = c - '0';
        return delta <= 9 && delta >= 0 ? delta : -1;
    }

}
