# MonVal - monetary amount conversions

One of the optimal ways to represent precise monetary amounts is by using an integer value of the smallest unit, such as cent.
For example, **23.50** becomes **2350**, and **-100.0** becomes **-10000**. 

This utility contains functions for converting such integer representations of monetary amounts to and from various JDK 
data types.

List of supported data types:

* CharSequence (from)
* String (to and from)
* double (to and from)
* long (from)
* int (from)
* BigInteger (from)
* BigDecimal (to and from)

## Reasoning behind implementation

The most convenient way of representing monetary data is creating a compound data-type which would provide a clear API
for value, currency codes, etc. There are several libraries with such functionality, such as Joda-Money.

This particular utility, however, caters to the cases where performance and memory consumption is more important than
API convenience. Thus, it avoids heap allocations wherever possible, and intentionally limits things like 
allowed formats for parsable strings.
 
It implies that java's **long** type will be used to represent money, and that it
that the monetary system consists of two units - "dollars" and "cents", where the "cents" have a value of 0.01 "dollar".
This covers most of the practical use-cases.
