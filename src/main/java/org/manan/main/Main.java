package org.manan.main;

import org.manan.scheduler.DailyScheduler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Main {
    public static void main(String[] args) throws SchedulerException {
        JobDetail j = JobBuilder.newJob(DailyScheduler.class).build();
        Trigger t = TriggerBuilder.newTrigger().withIdentity("CroneTrigger").withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever()).build();
        Scheduler s = StdSchedulerFactory.getDefaultScheduler();
        s.start();
        s.scheduleJob(j, t);
    }
}
