package com.bh.ecs.integration.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bh_ecs_stg_mass_upload_hist", schema = "bhecms")
public class BhEcsMassUploadHistory implements Serializable{

	private static final long serialVersionUID = 1519012374526199927L;

	@Id
	@Column(name = "load_number")
	private Integer loadNumber;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "status")
	private String status;
	
	@Column(name = "error_message")
	private String errorMessage;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "comments")
	private String comments;

	@Column(name = "wd_employee_id")
	private String wdEmployeeId;
	
	@Column(name = "last_updated_by")
	private String lastUpdatedBy;
	
	@Column(name = "last_updated_date")
	private Date lastUpdatedDate;
	
	public BhEcsMassUploadHistory() {
		super();
		
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getWdEmployeeId() {
		return wdEmployeeId;
	}

	public void setWdEmployeeId(String wdEmployeeId) {
		this.wdEmployeeId = wdEmployeeId;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getLoadNumber() {
		return loadNumber;
	}

	public void setLoadNumber(Integer loadNumber) {
		this.loadNumber = loadNumber;
	}



}
