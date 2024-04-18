package com.superdoc.checkdoctor.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class SuperDocJobConfig {

    static final String SUPER_DOC_JOB_NAME = "SuperDoc Result Job";
    private static final String SUPER_DOC_TRIGGER_NAME = "SuperDoc Result Trigger";

    @Value("${superdoc.job-config.job-cron}")
    private String superDocJobCron;

    @Bean
    public JobDetail filesResultJobDetail() {
        return JobBuilder.newJob().ofType(SuperDocJob.class)
                .withIdentity(SUPER_DOC_JOB_NAME)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger filesResultTrigger(JobDetail superDocResultJobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(SUPER_DOC_TRIGGER_NAME)
                .forJob(superDocResultJobDetail)
                .withSchedule(cronSchedule(superDocJobCron))
                .build();
    }
}
