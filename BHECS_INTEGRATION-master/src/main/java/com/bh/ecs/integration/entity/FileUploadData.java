package com.bh.ecs.integration.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 *
 */
@Entity
@Table(schema="bhecms" , name="file_upload")
public class FileUploadData implements Serializable{
	
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1519012374526199927L;

@Id
@Column(name = "upload_id", nullable = false)
private Integer id;

@Column(name = "file_id")private Integer fileId;
@Column(name = "file_name")private String fileName;
@Column(name = "csv_file_location")private String csvFileLocation;
@Column(name = "row_passed")private Integer rowPassed;
@Column(name = "row_failed")private Integer rowFailed;
@Column(name = "total_rows")private Integer toalRows;
@Column(name = "user_comment")private String userComments;
@Column(name = "db_processed")private Character processedFlag;
@Column(name = "file_format")private String fileFormat;
@Column(name = "archive_location")private String archiveLocation;

public String getFileFormat() {
	return fileFormat;
}


public void setFileFormat(String fileFormat) {
	this.fileFormat = fileFormat;
}


public Integer getId() {
	return id;
}


public void setId(Integer id) {
	this.id = id;
}



public String getFileName() {
	return fileName;
}


public void setFileName(String fileName) {
	this.fileName = fileName;
}

public String getCsvFileLocation() {
	return csvFileLocation;
}


public void setCsvFileLocation(String csvFileLocation) {
	this.csvFileLocation = csvFileLocation;
}


public Integer getRowPassed() {
	return rowPassed;
}


public void setRowPassed(Integer rowPassed) {
	this.rowPassed = rowPassed;
}


public Integer getRowFailed() {
	return rowFailed;
}


public void setRowFailed(Integer rowFailed) {
	this.rowFailed = rowFailed;
}


public Integer getToalRows() {
	return toalRows;
}


public void setToalRows(Integer toalRows) {
	this.toalRows = toalRows;
}


public String getUserComments() {
	return userComments;
}


public void setUserComments(String userComments) {
	this.userComments = userComments;
}


public Character getProcessedFlag() {
	return processedFlag;
}


public void setProcessedFlag(Character processedFlag) {
	this.processedFlag = processedFlag;
}



public Integer getFileId() {
	return fileId;
}


public void setFileId(Integer fileId) {
	this.fileId = fileId;
}
public String getArchiveLocation() {
	return archiveLocation;
}

public void setArchiveLocation(String archiveLocation) {
	this.archiveLocation = archiveLocation;
}
	
	

	

}
