package com.hfad.treasure;

import java.util.Date;
import java.sql.Timestamp;

public class TimeStamp {
    public static Timestamp currenttime()
    {
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts;
    }
}
