package com.example.sportsattendance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.Html;
import android.text.Spanned;



import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by neebal on 7/18/17.
 */
public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();
    private static final long MILLIS_OF_ONE_DAY = 86400000L;
    private static final int YEAR_1901 = 1901;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm a", Locale.US);
    private static final String START_OF_DAY = "00:00:00";
    private static final String END_OF_DAY = "23:59:59";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy, HH:mm:ss";



    static String ATLEAST_TWO_DIGIT = "%02d";

    String TWO_DECIMAL_DIGIT = "%.2f";

    String TWO_DECIMAL_DIGIT_PERCENTAGE = "%.0f%%";

    String TWO_DECIMAL_DIGIT_PERCENTAGE_PLUS = "+%.0f%%";

    String TWO_DECIMAL_DIGIT_WITH_PERCENTAGE_SYMBOL = "%.2f%%";

    public static long convertMillisToEpochTime(long timeInMillis) {
        return timeInMillis / 1000;
    }

    public static long convertEpochTimeToMillis(long timeInEpoch) {
        return timeInEpoch * 1000;
    }

    public static String getDisplayTimeWithAmPm(long timeInMillis) {
        return dateFormat.format(timeInMillis);
    }

    public static String getDisplayTime(long timeInMillis) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(timeInMillis);
        int hours = currentTime.get(Calendar.HOUR_OF_DAY);
        int minutes = currentTime.get(Calendar.MINUTE);
        int seconds = currentTime.get(Calendar.SECOND);
        String hr, min, sec;
        if (hours < 10) {
            hr = "0" + hours;
        } else {
            hr = String.valueOf(hours);
        }
        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = String.valueOf(minutes);
        }
        if (seconds < 10) {
            sec = "0" + seconds;
        } else {
            sec = String.valueOf(seconds);
        }
        return hr + ":" + min + ":" + sec;
    }

    public static String getDisplayMiniTime(int timeInMinutes) {
        return getFormattedTime(timeInMinutes / 60, timeInMinutes % 60, false);
    }

    public static String getDisplayMiniTime(long timeInMillis, boolean want12HourFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return getFormattedTime(calendar.get(want12HourFormat ? Calendar.HOUR : Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), want12HourFormat);
    }

    private static String getFormattedTime(int hour, int minute, boolean isTimeIn12HourFormat) {
        String displayFormat = ATLEAST_TWO_DIGIT + ":" + ATLEAST_TWO_DIGIT;

        if (isTimeIn12HourFormat && hour == 0) {
            hour = 12;
        }

        return String.format(Locale.getDefault(), displayFormat, hour, minute);
    }

    public static int convertMillisToMinutesTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);



        return (hours * 60) + minutes;
    }

    public static int getHoursFromMillis(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static long convertMinutesToMillis(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;

        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hours,
                mins,
                calendar.getActualMinimum(Calendar.SECOND));

        return calendar.getTimeInMillis();
    }

    private static int convertMinutesToHoursTime(int minutes) {

        int hours = minutes / 60;



        return hours;
    }

    public static boolean checkForNightSchedule(int minutes) {
        return DateUtils.convertMinutesToHoursTime(minutes) >= 19;

    }

    /**
     * Use this method to get display String for day's date.
     *
     * @param dateMillis date time in millis.
     * @return displayable String for day's date.
     */
    public static Spanned getDisplayDateTime(long dateMillis, boolean isWeekdayReq) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        String weekday = "";
        if (isWeekdayReq) {
            weekday = "- " + getSmallMonthName(calendar.get(Calendar.DAY_OF_WEEK));
        }
        return Html.fromHtml(date + "<sup><small>" + getDateSuffix(date) + "</small></sup> " + getShortMonthName(month) + " " + weekday + " " + getDisplayTime(dateMillis));
    }

    /**
     * Use this method to get display String for day's date.
     *
     * @param dateMillis date time in millis.
     * @return displayable String for day's date.
     */
    public static Spanned getDisplayDate(long dateMillis, boolean isWeekdayReq) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        String weekday = "";
        if (isWeekdayReq) {
            weekday = "- " + getSmallMonthName(calendar.get(Calendar.DAY_OF_WEEK));
        }
        return Html.fromHtml(date + "<sup><small>" + getDateSuffix(date) + "</small></sup> " + getShortMonthName(month) + " " + weekday);
    }

    /**
     * To get suffix of date.
     */
    private static String getDateSuffix(int date) {
        if (date >= 11 && date <= 13) {
            return "th";
        }
        switch (date % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * To get month name in small format.
     */
    private static String getShortMonthName(int month) {
        String[] months = DateFormatSymbols.getInstance().getShortMonths();

        return months[month];
    }

    private static String getMonthName(int month) {
        String[] months = DateFormatSymbols.getInstance().getMonths();

        return months[month];
    }

    /**
     * To get small weekday name.
     *
     * @param month month.
     * @return name of the weekday.
     */
    private static String getSmallMonthName(int month) {
        return DateFormatSymbols.getInstance().getShortWeekdays()[month];
    }

    public static String getDisplayLastSyncTime(long timeInMillis) {
        int[] differenceInTime = getDifferenceInTime(timeInMillis);
        String s;
        if (differenceInTime[0] > 0) {
            if (differenceInTime[0] == 1) {
                s = "Updated " + differenceInTime[0] + " month ago";
            } else {
                s = "Updated " + differenceInTime[0] + " months ago";
            }
        } else if (differenceInTime[1] > 0) {
            if (differenceInTime[1] == 1) {
                s = "Updated " + differenceInTime[1] + " day ago";
            } else {
                s = "Updated " + differenceInTime[1] + " days ago";
            }
        } else if (differenceInTime[2] > 0) {
            if (differenceInTime[2] == 1) {
                s = "Updated " + differenceInTime[2] + " hr ago";
            } else {
                s = "Updated " + differenceInTime[2] + " hrs ago";
            }
        } else {
            if (differenceInTime[3] == 1) {
                s = "Updated " + differenceInTime[3] + " min ago";
            } else {
                s = "Updated " + differenceInTime[3] + " mins ago";
            }
        }

        return s;
    }

    private static int[] getDifferenceInTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        calendar.setTimeInMillis(timeInMillis);
        int lastSyncedMonth = calendar.get(Calendar.MONTH);
        int lastSyncedDay = calendar.get(Calendar.DAY_OF_MONTH);
        int lastSyncedHour = calendar.get(Calendar.HOUR_OF_DAY);
        int lastSyncedMinute = calendar.get(Calendar.MINUTE);
        int[] differenceInTime = new int[4];
        differenceInTime[0] = currentMonth - lastSyncedMonth;
        differenceInTime[1] = currentDay - lastSyncedDay;
        differenceInTime[2] = currentHour - lastSyncedHour;
        differenceInTime[3] = currentMinute - lastSyncedMinute;
        return differenceInTime;
    }

    public static long getTimeAfter7Days(long timeInMillis) {
        return getTimeBeforeOrAfter(timeInMillis, 7);
    }

    public static long getTimeBefore10Days(long timeInMillis) {
        return getTimeBeforeOrAfter(timeInMillis, -10);
    }

    private static long getTimeBeforeOrAfter(long timeInMillis, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.add(Calendar.DAY_OF_WEEK, amount);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDayTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        makeEOD(calendar);

        return calendar.getTimeInMillis();
    }

    private static void makeEOD(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }

    private static void makeSOD(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
    }

    public static long convertMillisToEpochDay(long timeInMillis) {
        return timeInMillis / MILLIS_OF_ONE_DAY;
    }

    public static int getDayOfMonth(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfMonthBeforeOrAfter(long timeInMillis, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getFormattedDate(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        month++;

        int year = calendar.get(Calendar.YEAR);

        String monthString, dayString;

        if (ifSingleDigit(month)) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }

        if (ifSingleDigit(date)) {
            dayString = "0" + date;
        } else {
            dayString = String.valueOf(date);
        }

        return dayString + "-" + monthString + "-" + year;
    }

    private static boolean ifSingleDigit(int number) {
        return number < 10;
    }

    /**
     * @param dateInString String format: "1991-09-11T18:30:00Z"
     * @return dateInMillis
     */
    public static long convertDateTimeStringToMillis(String dateInString) {
        String yearString = "";
        String monthString = "";
        String dayOfMonthString = "";

        String[] spiltFromDate = dateInString.split("-");

        if (spiltFromDate.length > 0) {
            yearString = spiltFromDate[0];
        }
        if (spiltFromDate.length > 1) {
            monthString = spiltFromDate[1];
        }
        if (spiltFromDate.length > 2) {
            String[] splitFromTime = spiltFromDate[2].split("T");
            dayOfMonthString = splitFromTime[0];
        }

        int year;
        int month;
        int dayOfMonth;

        try {
            year = Integer.valueOf(yearString);
        } catch (NumberFormatException e) {
            year = 1990;
        }

        try {
            month = Integer.valueOf(monthString);
        } catch (NumberFormatException e) {
            month = 0;
        }

        try {
            dayOfMonth = Integer.valueOf(dayOfMonthString);
        } catch (NumberFormatException e) {
            dayOfMonth = 1;
        }

        Calendar calendar = Calendar.getInstance();
        makeSOD(calendar);

        calendar.set(year, month, dayOfMonth);

        return calendar.getTimeInMillis();
    }

    public static long getEpochStartDate() {
        Calendar calendar = Calendar.getInstance();
        makeSOD(calendar);
        calendar.set(1970, 0, 1);
        return calendar.getTimeInMillis();
    }

    public static int countAge(long dateOfBirthInMillis) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        long difference = currentTime - dateOfBirthInMillis;

        return (int) (difference / 31536000000L);
    }

    public static long generateTime(long dayInMillis, int dayTimeInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dayInMillis);
        makeSOD(calendar);
        calendar.add(Calendar.MINUTE, dayTimeInMinutes);

        return calendar.getTimeInMillis();
    }

    public static long getStartOfTheDayTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        makeSOD(calendar);
        return calendar.getTimeInMillis();
    }

    public static long convertEpochDayToMillis(long epochDay) {
        return epochDay * MILLIS_OF_ONE_DAY;
    }

    public static boolean isTimeComesUnderAmPeriod(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.HOUR_OF_DAY) < 12;
    }

    public static long changeTimeToAM(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR));
        return calendar.getTimeInMillis();
    }

    public static long changeTimeToPM(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        int calenderAmPmValue = calendar.get(Calendar.AM_PM);

        if (calenderAmPmValue == Calendar.PM) {
            return timeInMillis;
        }

        calendar.add(Calendar.HOUR_OF_DAY, 12);
        return calendar.getTimeInMillis();
    }

    public static String getReadingFileTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return String.format(Locale.getDefault(),
                "%d_%02d_%02d_%02d_%02d_%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }

    public static boolean isYearOlderThen1901(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return calendar.get(Calendar.YEAR) < YEAR_1901;
    }

    public static String getMonthYear(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return getMonthName(month) + " " + year;
    }

    public static long getStartTime(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        String startTime = date + ", " + START_OF_DAY;
        try {
            cal.setTime(Objects.requireNonNull(formatter.parse(startTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal.getTimeInMillis();
    }

    public static long getEndTime(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        String startTime = date + ", " + END_OF_DAY;
        try {
            cal.setTime(Objects.requireNonNull(formatter.parse(startTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal.getTimeInMillis();
    }

    public static String getDisplayDateWithTime(long timeInMillis) {
        String time = "0";
        if (timeInMillis != 0) {
            time = new SimpleDateFormat("dd-MM-yyyy, hh:mm a", Locale.US).format(timeInMillis);
        }
        return time;
    }

    public static String generateDateFromYears(int age) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.YEAR, -age);
        Date date = instance.getTime();
        DateFormat isoFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = isoFormat.format(date);
        return formattedDate.replace('-', '/');
    }

    public static Long getSODInMillis(long testTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(testTime);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTimeInMillis();
    }

    public static boolean isBelongToSameDate(long testTime1, long testTime2) {
        long time1 = getSODInMillis(testTime1);
        long time2 = getSODInMillis(testTime2);
        return time1 == time2;
    }

    public static String getDisplayValueFromDate(long timeInMillis, String datePattern) {
        String time = "0";
        if (timeInMillis != 0) {
            DateFormat dateFormatDDMMMYYYY = new SimpleDateFormat(datePattern, Locale.US);
            time = dateFormatDDMMMYYYY.format(timeInMillis);
        }
        return time;
    }

    public static long getMillisForNextSixMonths(long timeInMillis) {
        long millisForNextSix;

        if (timeInMillis == 0) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.add(Calendar.MONTH, 6);

        millisForNextSix = calendar.getTimeInMillis();

        return millisForNextSix;
    }
}