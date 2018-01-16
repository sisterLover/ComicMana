package mio.sis.com.comicmana.sdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import mio.sis.com.comicmana.sfile.ReadWritable;

/**
 * Created by Administrator on 2017/12/26.
 */

public class STime implements ReadWritable {
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
    public void GetCurrentTime() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        SetFromCalendar(gregorianCalendar);
    }

    @Override
    public void WriteStream(DataOutputStream stream) throws IOException {
        stream.writeInt(year);
        stream.writeInt(month);
        stream.writeInt(day);
        stream.writeInt(hour);
        stream.writeInt(minute);
        stream.writeInt(second);
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        year = stream.readInt();
        month = stream.readInt();
        day = stream.readInt();
        hour = stream.readInt();
        minute = stream.readInt();
        second = stream.readInt();
    }
}
