package com.bh.ecs.integration.dto;

public class FilePath {
	
	private String sourcePath;
	private String archivePath;
	private Character isActive;
	private String integrationType;
	
	public FilePath() {
		super();
	}
	public FilePath(String sourcePath, String archivePath, Character isActive, String integrationType) {
		super();
		this.sourcePath = sourcePath;
		this.archivePath = archivePath;
		this.isActive = isActive;
		this.integrationType = integrationType;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getArchivePath() {
		return archivePath;
	}
	public void setArchivePath(String archivePath) {
		this.archivePath = archivePath;
	}
	public Character getIsActive() {
		return isActive;
	}
	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}
	
	 public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}
	
	
}
