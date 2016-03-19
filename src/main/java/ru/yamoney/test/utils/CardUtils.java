package ru.yamoney.test.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by nizienko on 19.03.2016.
 */
public class CardUtils {
    public static boolean isCardNumberValid(String ccNumber)
    {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static boolean isCardExpired(Integer month, Integer year) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime( new Date());
        if (calendar.get((Calendar.YEAR)) < year) {
            return false;
        }
        else if (calendar.get((Calendar.YEAR)) == year) {
            if (calendar.get((Calendar.MONTH)) + 1 < month) {
                return false;
            }
        }
        return true;
    }
}
