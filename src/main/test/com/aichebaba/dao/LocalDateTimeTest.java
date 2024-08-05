package com.aichebaba.dao;

import junit.framework.TestCase;
import org.joda.time.LocalDateTime;

public class LocalDateTimeTest extends TestCase{

    public void testTime() {
        System.out.println(LocalDateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0));
    }
}
