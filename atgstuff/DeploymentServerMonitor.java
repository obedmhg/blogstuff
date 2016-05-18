package com.ocorp.util;

import atg.deployment.common.DeploymentException;
import atg.deployment.server.DeploymentServer;
import atg.deployment.server.Target;
import atg.service.scheduler.SchedulableService;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

/**
 * Scheduler that will notify via email the Status of BCC Sites, Staging and
 * production. It will notify Send information for each BCC site as follows:
 * 
 * Deployment Target 'Staging' Current installed agent snapshot : 131601 Current
 * state : IDLE Current Deployment Deployment projects : prj1639001 Deployment
 * Percentage : 32% Deployed from (server) : localhost:8080
 * 
 * @author Obed Murillo
 *
 */
public class DeploymentServerMonitor extends SchedulableService {

	private String jobName;
	private String jobDescription;
	private DeploymentServer deploymentServer;
	private Scheduler scheduler;
	private Schedule schedule;
	private EmailUtil emailUtil;

	private String emailSubject;

	private String emailFrom;

	private String[] emailRecipient;

	private HTMLMessageUtil htmlMessageUtil;

	private String percentCharUrl;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(final String jobName) {
		this.jobName = jobName;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(final String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public DeploymentServer getDeploymentServer() {
		return deploymentServer;
	}

	public void setDeploymentServer(final DeploymentServer deploymentServer) {
		this.deploymentServer = deploymentServer;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(final Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

	public EmailUtil getEmailUtil() {
		return emailUtil;
	}

	public void setEmailUtil(final EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(final String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(final String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String[] getEmailRecipient() {
		return emailRecipient;
	}

	public void setEmailRecipient(final String[] emailRecipient) {
		this.emailRecipient = emailRecipient;
	}

	public HTMLMessageUtil getHtmlMessageUtil() {
		return htmlMessageUtil;
	}

	public void setHtmlMessageUtil(final HTMLMessageUtil htmlMessageUtil) {
		this.htmlMessageUtil = htmlMessageUtil;
	}

	public String getPercentCharUrl() {
		return percentCharUrl;
	}

	public void setPercentCharUrl(final String percentCharUrl) {
		this.percentCharUrl = percentCharUrl;
	}

	@Override
	public void performScheduledTask(final Scheduler paramScheduler, final ScheduledJob paramScheduledJob) {
		sendMonitorNotification();
	}

	/**
	 * It will get information from deployment server, create an hmtl message
	 * and send it via email.
	 */
	public void sendMonitorNotification() {
		final StringBuilder htmlMessage = new StringBuilder();
		try {
			for (Target target : deploymentServer.getDeploymentTargets()) {
				htmlMessageUtil.createParagraph(htmlMessage, "Deployment Target '" + target.getName() + "'");
				htmlMessageUtil.createNewLine(htmlMessage);

				htmlMessageUtil.createParagraph(htmlMessage,
						"Current installed agent snapshot: " + target.getInstalledSnapshot());
				htmlMessageUtil.createParagraph(htmlMessage,
						"Current state          : " + target.getCurrentStatus().getStateString());
				if (null != target.getCurrentDeployment()) {
					htmlMessageUtil.createParagraph(htmlMessage, "Current Deployment");
					htmlMessageUtil.createParagraph(htmlMessage,
							"Deployment project     : " + target.getCurrentDeployment().getProjectIDs()[0]);
					if (null != target.getCurrentDeployment().getPercentageComplete()) {
						getPercentChart(target.getCurrentDeployment().getPercentageComplete(), htmlMessage);
					} else {
						getPercentChart("0", htmlMessage);
					}
				}
				htmlMessageUtil.createParagraph(htmlMessage,
						"Deployed from (server) : " + target.getCurrentStatus().getDeploymentServer());
				htmlMessageUtil.createParagraph(htmlMessage,
						"===========================================================================");

			}
		} catch (DeploymentException deploymentException) {
			htmlMessageUtil.createParagraph(htmlMessage,
					"There was a problem getting the deploymentStatus" + deploymentException.getMessage());
			vlogError(deploymentException,
					"There was an exception trying to send email with the Deployment Server monitor Status.");
		} finally {
			try {
				getEmailUtil().sendMimeEmail(getEmailRecipient(), getEmailSubject(), htmlMessage.toString(),
						getEmailFrom());
			} catch (Exception ex) {
				vlogError(ex, "There was an exception trying to send email with the Deployment Server monitor Status.");
			}
		}

	}

	/**
	 * It will return a chart using configured percentCharUrl with the percent
	 * completed and the remaining.
	 * 
	 * @param percentCompleted
	 * @param htmlMessage
	 */
	private void getPercentChart(final String percentCompleted, final StringBuilder htmlMessage) {
		String percentCharUrl = this.percentCharUrl;
		final String remaining = Integer.toString(100 - Integer.parseInt(percentCompleted));
		percentCharUrl = percentCharUrl.replace("{0}", percentCompleted);
		percentCharUrl = percentCharUrl.replace("{1}", remaining);
		htmlMessage.append("<img src= '" + percentCharUrl + "'/>");
	}

}
