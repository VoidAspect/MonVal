package com.voidaspect.monval;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MonetaryAmountTest {

    @Test
    void shouldParseAmount() {
        assertEquals(400, MonetaryAmount.parse("4"));
        assertEquals(400, MonetaryAmount.parse("04"));
        assertEquals(400, MonetaryAmount.parse("4.0"));
        assertEquals(400, MonetaryAmount.parse("4.00"));
        assertEquals(400, MonetaryAmount.parse("4.000"));
        assertEquals(400, MonetaryAmount.parse("0004.000"));
        assertEquals(400, MonetaryAmount.parse("4.005"));
        assertEquals(400, MonetaryAmount.parse("4.00asdf"));
        assertEquals(400, MonetaryAmount.parse("USD 4.005"));
        assertEquals(400, MonetaryAmount.parse("$ 4.00"));
        assertEquals(205, MonetaryAmount.parse("2.05"));
        assertEquals(250, MonetaryAmount.parse("2.5"));
        assertEquals(250, MonetaryAmount.parse("2.50"));
        assertEquals(250, MonetaryAmount.parse("2.501"));
        assertEquals(-100, MonetaryAmount.parse("-1"));
        assertEquals(-10, MonetaryAmount.parse("-0.1"));
        assertEquals(-1, MonetaryAmount.parse("-0.01"));
        assertEquals(-1, MonetaryAmount.parse("-$0.01"));
        assertEquals(-1, MonetaryAmount.parse("USD -0.01"));
        assertEquals(-1, MonetaryAmount.parse("- USD 0.01"));
        assertEquals(-1, MonetaryAmount.parse("-0.01 USD"));
        assertEquals(0, MonetaryAmount.parse("0"));
        assertEquals(0, MonetaryAmount.parse("00"));
        assertEquals(0, MonetaryAmount.parse("-0"));
        assertEquals(0, MonetaryAmount.parse("-00"));
        assertEquals(0, MonetaryAmount.parse("-0.0"));
        assertEquals(0, MonetaryAmount.parse("-0.00"));
        assertEquals(34568925, MonetaryAmount.parse("345,689.25"));
        assertEquals(34568925, MonetaryAmount.parse("345 689.25"));
    }

    @Test
    void shouldRejectInvalidStrings() {
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("abc"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("13abc"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("13.abc"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("13.3bc"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("--13.3"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("--"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("."));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("-."));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse("-.3"));
        assertThrows(NumberFormatException.class, () -> MonetaryAmount.parse(".3"));
    }

    @Test
    void shouldConvertAmountToString() {
        assertEquals("45.45", MonetaryAmount.toString(4545));
        assertEquals("45.40", MonetaryAmount.toString(4540));
        assertEquals("45.00", MonetaryAmount.toString(4500));
        assertEquals("45.05", MonetaryAmount.toString(4505));
        assertEquals("-45.45", MonetaryAmount.toString(-4545));
        assertEquals("0.45", MonetaryAmount.toString(45));
        assertEquals("-0.45", MonetaryAmount.toString(-45));
        assertEquals("-0.04", MonetaryAmount.toString(-4));
        assertEquals("-0.40", MonetaryAmount.toString(-40));
        assertEquals("0.00", MonetaryAmount.toString(0));
        assertEquals("0.00", MonetaryAmount.toString(-0));
    }

    @Test
    void shouldConvertAmountToStringWithCurrencyCode() {
        assertEquals("USD 45.45", MonetaryAmount.toString(4545, Currency.getInstance("USD")));
        assertEquals("EUR 45.45", MonetaryAmount.toString(4545, Currency.getInstance("EUR")));
    }

    @Test
    void shouldConvertAmountToStringWithCurrencySymbol() {
        Currency usCurrency = Currency.getInstance(Locale.US);
        Currency germanCurrency = Currency.getInstance(Locale.GERMANY);
        assertEquals(
                usCurrency.getSymbol() + " 45.45",
                MonetaryAmount.toStringWithCurrencySymbol(4545, usCurrency)
        );
        assertEquals(
                germanCurrency.getSymbol() + " 45.45",
                MonetaryAmount.toStringWithCurrencySymbol(4545, germanCurrency)
        );
    }

    @Test
    void shouldConvertFromLong() {
        assertEquals(4300, MonetaryAmount.from(43L));
        assertEquals(0, MonetaryAmount.from(0L));
    }

    @Test
    void shouldConvertFromInt() {
        assertEquals(4300, MonetaryAmount.from(43));
        assertEquals(0, MonetaryAmount.from(0));
    }

    @Test
    void shouldConvertFromBigInteger() {
        assertEquals(10000, MonetaryAmount.from(BigInteger.valueOf(100)));
        assertEquals(-10000, MonetaryAmount.from(BigInteger.valueOf(-100)));
        assertEquals(0, MonetaryAmount.from(BigInteger.ZERO));
        assertEquals(100, MonetaryAmount.from(BigInteger.ONE));
    }

    @Test
    void shouldConvertFromBigDecimal() {
        assertEquals(10000, MonetaryAmount.from(BigDecimal.valueOf(100)));
        assertEquals(0, MonetaryAmount.from(BigDecimal.ZERO));
        assertEquals(100, MonetaryAmount.from(BigDecimal.ONE));
        assertEquals(555555, MonetaryAmount.from(new BigDecimal("5555.55")));
        assertEquals(-555555, MonetaryAmount.from(new BigDecimal("-5555.55")));
        assertEquals(555555, MonetaryAmount.from(new BigDecimal("5555.556")));
        assertEquals(555555, MonetaryAmount.from(new BigDecimal("5555.555")));
        assertEquals(555555, MonetaryAmount.from(new BigDecimal("5555.554")));
    }

    @Test
    void shouldConvertToBigDecimal() {
        assertEquals(new BigDecimal("5555.55"), MonetaryAmount.toBigDecimal(555555));
        assertEquals(new BigDecimal("-5555.55"), MonetaryAmount.toBigDecimal(-555555));
        assertEquals(new BigDecimal("-5555.50"), MonetaryAmount.toBigDecimal(-555550));
        assertEquals(new BigDecimal("-5555.00"), MonetaryAmount.toBigDecimal(-555500));
        assertEquals(new BigDecimal("0.34"), MonetaryAmount.toBigDecimal(34));
        assertEquals(new BigDecimal("0.04"), MonetaryAmount.toBigDecimal(4));
        assertEquals(new BigDecimal("0.00"), MonetaryAmount.toBigDecimal(0));
    }

    @Test
    void shouldConvertFromDouble() {
        assertEquals(0, MonetaryAmount.from(0.0));
        assertEquals(0, MonetaryAmount.from(0.00));
        assertEquals(0, MonetaryAmount.from(0.001));
        assertEquals(34, MonetaryAmount.from(0.3411111111));
        assertEquals(45334, MonetaryAmount.from(453.341));
        assertEquals(45304, MonetaryAmount.from(453.041));
        assertEquals(45300, MonetaryAmount.from(453.001));
        assertEquals(45301, MonetaryAmount.from(453.01999999));
        assertEquals(-45301, MonetaryAmount.from(-453.01999999));
    }

    @Test
    void shouldConvertToDouble() {
        assertEquals(0.00, MonetaryAmount.toDouble(0));
        assertEquals(5.55, MonetaryAmount.toDouble(555));
        assertEquals(55.55, MonetaryAmount.toDouble(5555));
        assertEquals(555.55, MonetaryAmount.toDouble(55555));
        assertEquals(-555.55, MonetaryAmount.toDouble(-55555));
    }
}