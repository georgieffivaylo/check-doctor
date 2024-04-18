package com.superdoc.checkdoctor.config;

import com.superdoc.checkdoctor.rest.SuperDocService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
@AllArgsConstructor
@DisallowConcurrentExecution
public class SuperDocJob implements Job {

    private final SuperDocService superDocService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String doctorToCheck = "calendar/2282/today/7";
        superDocService.check(doctorToCheck).subscribe();
    }
}
