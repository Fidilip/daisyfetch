package cz.fs.proto1.reporting;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup // -- disable for now
public class ReportingScheduler {

	public static final String JOB_NAME = "reportingJob";
	
	@PostConstruct
	public void constructed() {
		System.out.println("ReportingScheduler initialized.");
	}
	
	@Schedule(hour="*", minute="*", second="30")
	public void doJob() {
		/*JobOperator jo = BatchRuntime.getJobOperator();
        jo.start("reportingJob", new Properties());
        System.out.println("Reporting job started.");*/
	}
	
}
