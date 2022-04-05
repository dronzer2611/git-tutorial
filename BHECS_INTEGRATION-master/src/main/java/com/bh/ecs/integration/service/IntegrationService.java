package com.bh.ecs.integration.service;


import java.util.*;
import com.bh.ecs.integration.dto.FilePath;
import com.bh.ecs.integration.dto.UploadResponseDTO;
import com.bh.ecs.integration.entity.FileUploadData;

public interface IntegrationService {
	
	public List<FilePath> getIntegrationPath();
	
	public String getMatchedTemplateDetailsByFileName(String fleName);
	
	public int insertFileDetailsIntoUploadTable(String templateName, String fileNme, String filePath, String userComments,String sso,String fileFormat, String archivedLocation);
	
	public List<FileUploadData> getFileUploadDetailsFromUploadTable();
	
	public UploadResponseDTO putDocumentOnPostgres(String templateId, String upladId, String flName,String filePath,Integer uploadIdFrmTbl,String archiveLocation);
	
	public List<Map<String, String>> getTemplateDetailsFromFileMasterTable(String templateId);
	
	public Map<String, String> insertFileDataIntoStagingTable(List<String> list);
	
	public int updateFileUploadToUploadTable(String upldID, String fileId, Map<String, String> errResLogMap, String targetFileLocation);
	
	public int insertFileUploadLoggerData(int uplodId, String errLog);
	
	public String processReferenceCodeToRefMasterTable();
	
	public String processGolnetCodeToGLNMasterTable();
	
	public String processFDLCodeToFDLMasterTable();
	
	public String updateGoldIds();
	
	public String processProjectCodeDataToPRMAsterTable();
	
	public String processUEICodeDataToMasterTable();
	
	public String processWorkdayQCDataToApplicationTables();
	
	public String processWorkdayDataToApplicationTables();
	
	public void getHRDataLakeOutboundData();
	
	public void getWorkdayOutboundData();
	
	public String decryptJob(String filNme,String fileOutputNme);
	
	public void getCostingReportDaily();
	
	public void getWorkdayOutboundDataBH();
	
	public String executeAutoApproveCostingBasedOnCutoffDate();
	
	public void sendMail();
}