package com.bh.ecs.integration.dao;

import java.util.List;
import java.util.Map;
import com.bh.ecs.integration.dto.CostingReport;
import com.bh.ecs.integration.dto.EmailContentDto;
import com.bh.ecs.integration.dto.EmpDetails;
import com.bh.ecs.integration.dto.FilePath;
import com.bh.ecs.integration.dto.TemplateDto;
import com.bh.ecs.integration.dto.WorkdayData;
import com.bh.ecs.integration.entity.FileUploadData;


public interface IntegrationDao {
	
	public List<FilePath> getIntegrationPath();

	public List<TemplateDto> getMatchedTemplateDetailsByFileName();
	
	public int insertFileDetailsIntoUploadTable(String templateName, String fileName, String filePath, String userComments,String sso,String fileFormat, String archivedLocation);
	
	public List<FileUploadData> getFileUploadDetailsFromUploadTable();
	
	public List<Map<String, String>> getTemplateDetailsFromFileMasterTable(String templateId);
	
	public Map<String, String> insertFileDataIntoStagingTable(List<String> list);
	
	public int updateFileUploadToUploadTable(String upldID, String fileId, Map<String, String> errResLogMap, String targetFileLocation);
	
	public int insertFileUploadLoggerData(int uploadId, String errLog);
	
	public String processReferenceCodeToRefMasterTable();
	
	public String processGolnetCodeToGLNMasterTable();
	
    public String processFDLCodeToFDLMasterTable();
    
    public String updateGoldIds();
	
	public String processProjectCodeDataToPRMAsterTable();
	
	public String processUEICodeDataToMasterTable();
	
	public String processWorkdayDataToApplicationTables();
	
	public String processWorkdayQCDataToApplicationTables();
	
	public List<EmpDetails> getHRDataLake();
	
	public List<WorkdayData> getWorkdayOutboundData();
	
	public List<CostingReport> getCostingReport();
	
	public List<WorkdayData> getWorkdayOutboundDataBH();
	
	public String executeAutoApproveCostingBasedOnCutoffDate();
	
	public List<EmailContentDto> getEmployeeDetails();
	
	public List<EmailContentDto> getQueueCount(String wdEmpId);
	
}
