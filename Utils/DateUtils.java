package com.example.wayfuel.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getDayOfWeek(String inputDateString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            Date date = inputDateFormat.parse(inputDateString);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE", Locale.US);
            return outputDateFormat.format(date);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        String inputDateString = "2023-08-12";
        String dayOfWeek = getDayOfWeek(inputDateString);

        if (dayOfWeek != null) {
            System.out.println("Day of the week: " + dayOfWeek);
        }
    }
}
