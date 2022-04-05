package com.bh.ecs.integration.dto;

import java.io.Serializable;
import java.util.List;

public class ExtractFileDataDTO implements Serializable {
	private static final long serialVersionUID = 1519012374526199927L;

	private List<String> fileData;
	private String status;
	private String errorLog;

	public List<String> getFileData() {
		return fileData;
	}

	public void setFileData(List<String> fileData) {
		this.fileData = fileData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

}
