package com.bh.ecs.integration.dto;


public class EmailContentDto {
	private String fromEmail;
	private String toEmail;
	private String applicationUrl;
	private String applicationsupportUrl;
	private String employeeId;
	private String employeeName;
	private String ssoId;
	private String employeeEmail;
	private String requestType;
	private String value;
	private String ownerSso;
	private String ownerEmail;
	private String approverEmail;
	private String lastUpdatedName;
	private String lastUpdatedEmail;
	private String lastUpdatedSso;
	private String ccEmail;
	private String attachmentName;
	
	
	public String getSsoId() {
		return ssoId;
	}
	public void setSsoId(String ssoId) {
		this.ssoId = ssoId;
	}
	public String getEmployeeEmail() {
		return employeeEmail;
	}
	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getApplicationsupportUrl() {
		return applicationsupportUrl;
	}
	public void setApplicationsupportUrl(String applicationsupportUrl) {
		this.applicationsupportUrl = applicationsupportUrl;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public String getToEmail() {
		return toEmail;
	}
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
	public String getApplicationUrl() {
		return applicationUrl;
	}
	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}

	public String getOwnerSso() {
		return ownerSso;
	}
	public void setOwnerSso(String ownerSso) {
		this.ownerSso = ownerSso;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public String getApproverEmail() {
		return approverEmail;
	}
	public void setApproverEmail(String approverEmail) {
		this.approverEmail = approverEmail;
	}
	public String getLastUpdatedName() {
		return lastUpdatedName;
	}
	public void setLastUpdatedName(String lastUpdatedName) {
		this.lastUpdatedName = lastUpdatedName;
	}
	public String getLastUpdatedEmail() {
		return lastUpdatedEmail;
	}
	public void setLastUpdatedEmail(String lastUpdatedEmail) {
		this.lastUpdatedEmail = lastUpdatedEmail;
	}
	public String getLastUpdatedSso() {
		return lastUpdatedSso;
	}
	public void setLastUpdatedSso(String lastUpdatedSso) {
		this.lastUpdatedSso = lastUpdatedSso;
	}
	public String getCcEmail() {
		return ccEmail;
	}
	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

}
