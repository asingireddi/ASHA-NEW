package com.miraclesoft.scvp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * This is date Utility class. will provide instances of current time stamp and
 * used for date format changing.
 *
 * @author Narendar Geesidi
 */
public class DateUtility {

    /**
     * Date view to DB compare.
     *
     * @param viewDate the view date
     * @return the string
     */
    public static String convertToSqlDate(final String viewDate) {
        StringTokenizer st = new StringTokenizer(viewDate);
        String date = st.nextToken();
        String time = st.nextToken();
        st = new StringTokenizer(date, "/");
        String month = st.nextToken();
        String date1 = st.nextToken();
        String year = st.nextToken();
        String covertedDate = year + "-" + month + "-" + date1;
        return covertedDate + " " + time + ":00.0";
    }

    /**
     * Gets the today with custom time.
     *
     * @param hour the hour
     * @param minutes the minutes
     * @param seconds the seconds
     * @return the today with custom time
     */
    public static String getTodayWithCustomTime(final int hour, final int minutes, final int seconds) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return simpleDateFormat.format(calendar.getTime());
    }
}
