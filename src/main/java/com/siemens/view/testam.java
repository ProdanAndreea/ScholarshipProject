package com.siemens.view;

import com.siemens.model.Recovery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class testam {

    static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {

        String name = "GRIGORE%20DRAGOS%20ALEXANDRU";
        name = name.replaceAll("%20", " ");
        System.out.println(name);

        String dataToBeDeciphered = "20082019";
        LocalDate leaveDate = decipherDate(Integer.parseInt(dataToBeDeciphered));
        System.out.println(leaveDate);

        String oraToBeDeciphered = "0300";
        System.out.println(decipherHours(Integer.parseInt(oraToBeDeciphered)));

        String recoveriesList = "25082019n0300m28082019n0030";
        System.out.println(decipherRecoveries(leaveDate, recoveriesList));
    }


    private static LocalDate decipherDate(int dateInInt) {
        int year = 0;

        for (int i = 1; i <= 1000; i*=10) {
            int c = dateInInt % 10 * i;
            year += c;
            dateInInt/=10;
        }

        int month = 0;

        for (int i = 1; i <= 10; i*=10) {
            int c = dateInInt % 10 * i;
            month += c;
            dateInInt/=10;
        }

        String monthString = String.valueOf(month);
        if (monthString.length() == 1)
            monthString = "0" + monthString;

        int day = 0;

        for (int i = 1; i <= 10; i*=10) {
            int c = dateInInt % 10 * i;
            day += c;
            dateInInt/=10;
        }

        String dayString = String.valueOf(day);
        if (dayString.length() == 1)
            dayString = "0" + dayString;

        String finalDateString = dayString + "-" + monthString + "-" + year;

        return LocalDate.parse(finalDateString, format);
    }
    private static LocalTime decipherHours(int hourInInt) {

        int minutes = 0;

        for (int i = 1; i <= 10; i*=10) {
            int c = hourInInt % 10 * i;
            minutes += c;
            hourInInt/=10;
        }

        String minutesString = String.valueOf(minutes);
        if (minutesString.length() == 1)
            minutesString = "0" + minutesString;

        int hour = 0;

        for (int i = 1; i <= 10; i*=10) {
            int c = hourInInt % 10 * i;
            hour += c;
            hourInInt/=10;
        }

        String hourString = String.valueOf(hour);
        if (hourString.length() == 1)
            hourString = "0" + hourString;


        String finalHourTime = hourString + ":" + minutesString;
        return LocalTime.parse(finalHourTime, hourFormatter);
    }

    private static List<Recovery> decipherRecoveries(LocalDate leaveDate, String recoveriesCode) {
        List<Recovery> recoveries = new ArrayList<Recovery>();

        String[] recoveriesString = recoveriesCode.split("m");

        for (String recovery : recoveriesString) {
            String[] dateAndTime = recovery.split("n");
            LocalDate dateRecovery = decipherDate(Integer.parseInt(dateAndTime[0]));
            LocalTime timeRecovery = decipherHours(Integer.parseInt(dateAndTime[1]));
            recoveries.add(new Recovery(leaveDate, dateRecovery, timeRecovery));
        }

        return recoveries;
    }

}
