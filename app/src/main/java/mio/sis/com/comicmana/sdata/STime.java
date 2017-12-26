package mio.sis.com.comicmana.sdata;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2017/12/26.
 */

public class STime {
    //  data
    public int year, month, day, hour, minute, second;

    //  function
    public STime() {
        year = 1990;
        month = 1;
        day = 1;
        hour = minute = second = 0;
    }
    public GregorianCalendar GetCalendar() {
        return new GregorianCalendar(year, month, day, hour, minute, second);
    }
    public void SetFromCalendar(GregorianCalendar calendar) {
        year = calendar.get(GregorianCalendar.YEAR);
        month = calendar.get(GregorianCalendar.MONTH);
        day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        minute = calendar.get(GregorianCalendar.MINUTE);
        second = calendar.get(GregorianCalendar.SECOND);
    }
}
