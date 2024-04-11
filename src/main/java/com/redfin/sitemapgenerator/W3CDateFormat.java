/**
 *
 */
package com.redfin.sitemapgenerator;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * <p>Formats and parses dates in the six defined W3C date time formats.  These formats are described in 
 * "Date and Time Formats",
 * <a href="https://www.w3.org/TR/NOTE-datetime">https://www.w3.org/TR/NOTE-datetime</a>.</p>
 *
 * <p>The formats are:
 *
 * <ol>
 * <li>YEAR: YYYY (eg 1997)
 * <li>MONTH: YYYY-MM (eg 1997-07)
 * <li>DAY: YYYY-MM-DD (eg 1997-07-16)
 * <li>MINUTE: YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
 * <li>SECOND: YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
 * <li>MILLISECOND: YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
 * </ol>
 *
 * Note that W3C timezone designators (TZD) are either the letter "Z" (for GMT) or a pattern like "+00:30" or "-08:00".  This is unlike
 * RFC 822 timezones generated by SimpleDateFormat, which omit the ":" like this: "+0030" or "-0800".</p>
 *
 * <p>This class allows you to either specify which format pattern to use, or (by default) to
 * automatically guess which pattern to use (AUTO mode).  When parsing in AUTO mode, we'll try parsing using each pattern
 * until we find one that works.  When formatting in AUTO mode, we'll use this algorithm:
 *
 * <ol><li>If the date has fractional milliseconds (e.g. 2009-06-06T19:49:04.45Z) we'll use the MILLISECOND pattern
 * <li>Otherwise, if the date has non-zero seconds (e.g. 2009-06-06T19:49:04Z) we'll use the SECOND pattern
 * <li>Otherwise, if the date is not at exactly midnight (e.g. 2009-06-06T19:49Z) we'll use the MINUTE pattern
 * <li>Otherwise, we'll use the DAY pattern.  If you want to format using the MONTH or YEAR pattern, you must declare it explicitly.
 * </ol>
 *
 * @author Dan Fabulich
 * @see <a href="https://www.w3.org/TR/NOTE-datetime">Date and Time Formats</a>
 */
public class W3CDateFormat {
    public static final DateTimeFormatter FORMAT_RFC_822 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz").withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_AUTO = new DateTimeFormatterBuilder()
            .appendPattern("yyyy[-MM[-dd]]['T'HH[:mm[:ss[.SSS]]]][XXX]")
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat AUTO = new W3CDateFormat(FORMAT_AUTO, true, ZoneOffset.UTC,true);

    private static final DateTimeFormatter FORMAT_MILLISECONDS = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]")
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat MILLISECOND = new W3CDateFormat(FORMAT_MILLISECONDS, true, ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_SECONDS = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss[XXX]")
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat SECOND = new W3CDateFormat(FORMAT_SECONDS, true, ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_MINUTES = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm[XXX]")
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat MINUTE = new W3CDateFormat(FORMAT_MINUTES, true, ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_DAYS = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat DAY = new W3CDateFormat(FORMAT_DAYS, false, ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_MONTHS = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM")
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat MONTH = new W3CDateFormat(FORMAT_MONTHS, false, ZoneOffset.UTC);

    private static final DateTimeFormatter FORMAT_YEAR = new DateTimeFormatterBuilder()
            .appendPattern("yyyy")
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter(Locale.ENGLISH);
    public static final W3CDateFormat YEAR = new W3CDateFormat(FORMAT_YEAR, false, ZoneOffset.UTC);

    private static final List<W3CDateFormat> ALL_FORMATS = List.of(AUTO, MILLISECOND, SECOND, MINUTE, DAY, MONTH, YEAR);


    private final DateTimeFormatter dateTimeFormatter;
    private final boolean includeTimeZone;
    private final ZoneId timeZone;
    private final boolean isAuto;


    public W3CDateFormat() {
        this.dateTimeFormatter = FORMAT_AUTO;
        isAuto = true;
        this.includeTimeZone = true;
        this.timeZone = ZoneOffset.UTC;
    }

    public W3CDateFormat(DateTimeFormatter formatter, boolean includeTimeZone, ZoneId zoneId) {
        this.dateTimeFormatter = formatter;
        this.includeTimeZone = includeTimeZone;
        this.timeZone = zoneId;
        this.isAuto = false;
    }

    private W3CDateFormat(DateTimeFormatter formatter, boolean includeTimeZone, ZoneId zoneId,boolean isAuto){
        this.dateTimeFormatter = formatter;
        this.includeTimeZone = includeTimeZone;
        this.timeZone = zoneId;
        this.isAuto = isAuto;
    }

    public OffsetDateTime parse(final String date) {
        return OffsetDateTime.parse(date, dateTimeFormatter);
    }

    public W3CDateFormat withZone(final ZoneId zone) {
        return new W3CDateFormat(dateTimeFormatter, includeTimeZone, zone,isAuto);
    }

    public String format(OffsetDateTime date) {
        if (!ZoneOffset.UTC.equals(timeZone)) {
            date = date.atZoneSameInstant(timeZone).toOffsetDateTime();
        }

        DateTimeFormatter formatter = dateTimeFormatter;
        if (isAuto) {
            formatter = autoFormat(date);
        }
        if (includeTimeZone) {
            return formatter.withZone(timeZone).format(date);
        } else {
            return formatter.format(date);
        }

    }


    private DateTimeFormatter autoFormat(OffsetDateTime date) {
        if (date.get(ChronoField.MILLI_OF_SECOND) > 0) {
            return FORMAT_MILLISECONDS;
        } else if (date.getSecond() > 0) {
            return FORMAT_SECONDS;
        } else if ((date.getHour() + date.getMinute()) > 0) {
            return FORMAT_MINUTES;
        } else {
            return FORMAT_DAYS;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof W3CDateFormat)) return false;
        if (!super.equals(o)) return false;
        W3CDateFormat that = (W3CDateFormat) o;
        return dateTimeFormatter == that.dateTimeFormatter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateTimeFormatter);
    }


}