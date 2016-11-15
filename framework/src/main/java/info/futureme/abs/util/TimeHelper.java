package info.futureme.abs.util;

import android.text.format.Time;

public class TimeHelper {
    public static final int NANOS_PER_MS = 1000000;
    public static final int MS_PER_SECOND = 1000;
    public static final int SECOND_PER_MIN = 60;
    public static final int MIN_PER_HOUR = 60;
    public static final int HOUR_PER_DAY = 24;
    /**
     * Milliseconds per min
     */
    public static final long MS_PER_MIN = TimeHelper.MS_PER_SECOND//
            * TimeHelper.SECOND_PER_MIN;
    /**
     * Milliseconds per hour
     */
    public static final long MS_PER_HOUR = TimeHelper.MIN_PER_HOUR//
            * TimeHelper.MS_PER_MIN;
    /**
     * Milliseconds per day
     */
    public static final long MS_PER_DAY = MS_PER_HOUR * TimeHelper.HOUR_PER_DAY;

    // Now

    public static Time now() {
        Time now = new Time();
        now.setToNow();
        return now;
    }

    public static Time hoursFromNow(int hours) {
        Time now = now();
        offsetHours(now, hours);
        return now;
    }

    public static Time minsFromNow(int mins) {
        Time now = now();
        offsetMins(now, mins);
        return now;
    }

    public static Time secondsFromNow(int seconds) {
        Time now = now();
        offsetSeconds(now, seconds);
        return now;
    }

    /**
     * Get time offset in seconds of (now-t)
     */
    public static int since(Time t) {
        return minus(now(), t);
    }

    // Creators

    public static Time date(int year, int month, int monthDay) {
        Time t = new Time();
        t.year = year;
        t.month = month;
        t.monthDay = monthDay;
        return t;
    }

    public static Time time(int hour, int minute, int second) {
        Time t = new Time();
        t.hour = hour;
        t.minute = minute;
        t.second = second;
        return t;
    }

    // Setters

    public static Time setDateOnly(Time t, int year, int month, int monthDay) {
        t.year = year;
        t.month = month;
        t.monthDay = monthDay;
        return t;
    }

    public static Time setTimeOnly(Time t, int hour, int minute, int second) {
        t.hour = hour;
        t.minute = minute;
        t.second = second;
        return t;
    }

    // Operations

    /**
     * Get time offset in seconds of (a-b)
     */
    public static int minus(Time a, Time b) {
        return (int) ((a.toMillis(true) - b.toMillis(true)) / MS_PER_SECOND);
    }

    public static void offsetDays(Time t, int days) {
        t.monthDay += days;
        t.normalize(true);
    }

    public static void offsetHours(Time t, int hours) {
        t.hour += hours;
        t.normalize(true);
    }

    public static void offsetMins(Time t, int mins) {
        t.minute += mins;
        t.normalize(true);
    }

    public static void offsetSeconds(Time t, int seconds) {
        t.second += seconds;
        t.normalize(true);
    }

    public static int compareWithoutDate(Time a, Time b) {
        int hourOffset = a.hour - b.hour;
        if (hourOffset == 0) {
            int minOffset = a.minute - b.minute;
            if (minOffset == 0) {
                return a.second - b.second;
            } else {
                return minOffset;
            }

        } else {
            return hourOffset;
        }
    }
}
