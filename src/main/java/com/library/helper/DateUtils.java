package com.library.helper;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static Date getToday() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(DateTimeUtils.currentTimeMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date addMonths(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static Date addYears(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    public static Date getDateWithoutTimeUsingCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static long getDayDiffInDates(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) return 0;
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff;
    }

    public static long getDayDiffInHours(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) return 0;
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff;
    }


    public static Date setTimeToDate(Date date, Date time) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
        return dateCal.getTime();
    }

    public static Date removeTimeFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String getFinancialYear(Date d) {
        int month;
        int year;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        //calendar will start from 0-11 [0=jan, 11=dec]
        month = cal.get(Calendar.MONTH);
        int advance = (month < 3) ? 1 : 0;
        year = cal.get(Calendar.YEAR) - advance;
        return year + "-" + String.valueOf(year + 1).substring(2);
    }

    public static int getMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        //cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getYear(Date d) {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    //    public static Date getDateOnStartOfWeek(Date d) {
//        LocalDate ld = LocalDate.fromDateFields(d);
//        while (ld.getDayOfWeek() != DayOfWeek.MONDAY.getValue()) {
//            ld = ld.minusDays(1);
//        }
//        return ld.toDate();
//    }
//    public static Date getDateOnEndOfWeek(Date d) {
//        LocalDate ld = LocalDate.fromDateFields(d);
//        while (ld.getDayOfWeek() != DayOfWeek.SUNDAY.getValue()) {
//            ld = ld.plusDays(1);
//        }
//        return ld.toDate();
//    }
//
    public static Date getDateOnStartOfMonth(Date d) {
        LocalDate ld = LocalDate.fromDateFields(d);
        return ld.withDayOfMonth(1).toDate();
    }
//    public static Date getDateOnEndOfMonth(Date d) {
//        LocalDate ld = LocalDate.fromDateFields(d);
//        return ld.plusMonths(1).withDayOfMonth(1).minusDays(1).toDate();
//
//    }

    public static String formatDate(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static String getGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 1 && timeOfDay < 12) {
            return "Good morning";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            return "Good afternoon";
        } else
            return "Good evening";
    }

    public static int getAge(Date dob) {
        if (dob == null) return 0;
        return Period.between(java.time.LocalDate.ofInstant(Instant.ofEpochMilli(dob.getTime()), TimeZone.getDefault().toZoneId()), java.time.LocalDate.now()).getYears();
    }


    public static List<Date> getFromAndToDateByRange(Date fromDate, Date toDate) {

        List<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        cal.setTime(fromDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date normalizedFromDate = cal.getTime();


        cal.setTime(toDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date normalizedToDate = cal.getTime();


        long diffInMillis = normalizedToDate.getTime() - normalizedFromDate.getTime();
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1;


        cal.setTime(normalizedFromDate);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date prevToDate = cal.getTime();


        cal.setTime(prevToDate);
        cal.add(Calendar.DATE, (int) -(days - 1));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date prevFromDate = cal.getTime();

        dates.add(prevFromDate);
        dates.add(prevToDate);

        return dates;
    }

    public static List<Date> getDateFromAndToDateByInterval(Date fromDate, Integer intervalInDays) {

        if (fromDate == null || intervalInDays == null) {
            return Collections.emptyList();
        }

        Calendar calendar = Calendar.getInstance();

        // Start of day
        calendar.setTime(fromDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startDate = calendar.getTime();

        // End date
        calendar.add(Calendar.DAY_OF_MONTH, intervalInDays);

        // End of day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        Date endDate = calendar.getTime();

        return Arrays.asList(endDate, startDate);
    }

    public static List<Date> getFromAndToDate(Date fromDate, Date toDate) {
        List<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        cal.setTime(fromDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        dates.add(cal.getTime());


        cal.setTime(toDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        dates.add(cal.getTime());

        return dates;

    }

    public Long getHoursDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null) {
            return null;
        }

        long diffInMillis = toDate.getTime() - fromDate.getTime();

        return TimeUnit.MILLISECONDS.toHours(diffInMillis);
    }

}
