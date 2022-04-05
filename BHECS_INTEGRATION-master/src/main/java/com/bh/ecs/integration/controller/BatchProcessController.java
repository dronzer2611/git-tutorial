package com.bh.ecs.integration.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import javax.servlet.ServletContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.bh.ecs.integration.dto.FilePath;
import com.bh.ecs.integration.dto.UploadResponseDTO;
import com.bh.ecs.integration.entity.FileUploadData;
import com.bh.ecs.integration.service.IntegrationService;

@Component
public class BatchProcessController {
	@Autowired
	IntegrationService integrationService;

	@Autowired
	ServletContext context;

	private Logger log = LoggerFactory.getLogger(BatchProcessController.class);
	
	
	@Scheduled(cron = "0 30 0 * * *",zone="CST")//Every day at 12:30AM CST--PROD
	public void executeDRMAndFDLFirstJob() {
		log.info("First Scheduler for DRM and FDL :: Start");
		List<FilePath> filePaths = integrationService.getIntegrationPath();//To fetch the integration paths
		for(FilePath filePath : filePaths) {
			//to check if the integration path that fetched is to consider only inbound and other than workday
			if("INBOUND".equalsIgnoreCase(filePath.getIntegrationType()) && !filePath.getSourcePath().contains("workday")) {
			File file = new File(filePath.getSourcePath());
			FilenameFilter filesNameFilter = (File dir, String name) -> name.toUpperCase().endsWith(".CSV");
			File[] fileList = file.listFiles(filesNameFilter);
			log.info("No. of files present in the path {}",fileList.length);
			for (File fileOne : fileList) {
				//to check whether the File name contains the template name as in DB
				String templateName = integrationService.getMatchedTemplateDetailsByFileName(fileOne.getName());
				String sso = "SYSTEM";
				String commentsForFile;
				String fileFormat = FilenameUtils.getExtension(fileOne.getName());
				commentsForFile = (templateName == null) ? "Template Does not Exists" :  "Template Exists";
				//to insert into file upload table with file details
				int res = integrationService.insertFileDetailsIntoUploadTable(templateName, fileOne.getName(), fileOne.getAbsolutePath(),
						commentsForFile, sso,fileFormat,filePath.getArchivePath());
				log.info("No. of Rows updated in File Upload table {}  ",res);
			}
			}
		
		}
		log.info("First Scheduler for DRM and FDL :: End");
	}


	@Scheduled(cron = "0 40 0 * * *",zone="CST")//Every day at 12:40AM of CST--PROD
	public void executeDRMAndFDLSecondJob() {
		log.info("Second Scheduler for DRM and FDL:: Start");
		List<FileUploadData> fileUploadDataList = integrationService.getFileUploadDetailsFromUploadTable();
		if (!fileUploadDataList.isEmpty()) {
			for (FileUploadData fileUploadData : fileUploadDataList) {	
				if(!fileUploadData.getFileName().contains("WD_BHECS")) {
				UploadResponseDTO response = integrationService.putDocumentOnPostgres(fileUploadData.getFileId() + "",
						fileUploadData.getId() + "", fileUploadData.getFileName(), fileUploadData.getCsvFileLocation(),fileUploadData.getId(),fileUploadData.getArchiveLocation());
				log.info("Data inserted to Staging Tables: {}",response);
				}
			}
			//to store staging table data to master table
			// Process Reference Code
			log.info("Process Reference Code Data from Staging Table to Master Table:: Start");
			String messageRF = integrationService.processReferenceCodeToRefMasterTable();
			log.info("Process Reference Code Data from Staging Table to Master Table:: End",messageRF);
			//Process GoldNet Code
			log.info("Process GoldNet Code Data from Staging Table to Master Table::Start");
			String messageGLE = integrationService.processGolnetCodeToGLNMasterTable();
			log.info("Process GoldNet Code Data from Staging Table to Master Table::End",messageGLE);
			//Process Project Code
			log.info("Process Project Code Data from Staging Table to Master Table::Start");
			String messagePR = integrationService.processProjectCodeDataToPRMAsterTable();
			log.info("Process Project Code Data from Staging Table to Master Table::End",messagePR);
			//Process UEI Code 
			log.info("Process UEI Code Data from Staging Table to Master Table::Start");
			String messageUEI = integrationService.processUEICodeDataToMasterTable();
			log.info("Process Project Code Data from Staging Table to Master Table::End",messageUEI);
			//Process FDL Code
			log.info("Process FDL Code Data from Staging Table to Master Table::Start");
			String messageFDL = integrationService.processFDLCodeToFDLMasterTable();
			log.info("Process FDL Code Data from Staging Table to Master Table::End",messageFDL);
			//Update Gold Ids based on FDL Mapping and GLE Mapping
			log.info("Update Gold Ids based on FDL Mapping and GLE Mapping::Start");
			String messageGlid = integrationService.updateGoldIds();
			log.info("Update Gold Ids based on FDL Mapping and GLE Mapping::End",messageGlid);
		
			
		}
		log.info("Second Scheduler for DRM and FDL :: End");
	}
	
	@Scheduled(cron = "0 30 2 * * *",zone="CST")//Every day at 02:30AM of CST--PROD
	public void executeWorkdayInboundJob() {
		log.info("Scheduler for Workday Inbound :: Start");
		List<FilePath> filePaths = integrationService.getIntegrationPath();
		for(FilePath filePath : filePaths) {
			if("INBOUND".equalsIgnoreCase(filePath.getIntegrationType()) && filePath.getSourcePath().contains("workday")) {
			File file = new File(filePath.getSourcePath());
			FilenameFilter fileNameFilter = (File dir, String name) -> name.toUpperCase().endsWith(".CSV.PGP");
			File[] fileList = file.listFiles(fileNameFilter);
			log.info("No. of files present in the path {}",fileList.length);
			for (File fileOne : fileList) {
				String templateName = integrationService.getMatchedTemplateDetailsByFileName(fileOne.getName());
				String sso = "SYSTEM";
				String comment;
				String fileFormat = FilenameUtils.getExtension(fileOne.getName());
				  comment = (templateName == null) ? "Template Does not Exists" :  "Template Exists";
				//code to accept .csv.pgp files with encryption
				  try {//code to accept .csv.pgp files with encryption
						int lastDot = fileOne.getAbsolutePath().lastIndexOf('.');
						String targetFileName = fileOne.getAbsolutePath().substring(0,lastDot);
						integrationService.decryptJob(fileOne.getAbsolutePath(),targetFileName);
						File filetwo = new File(targetFileName);
						int res=integrationService.insertFileDetailsIntoUploadTable(templateName,filetwo.getName(), filetwo.getAbsolutePath(),
								comment, sso,fileFormat,filePath.getArchivePath());
						//fileOne.delete();
						String targetFile = filePath.getArchivePath()+"/"+fileOne.getName();
						Files.move(Paths.get(fileOne.getAbsolutePath()), Paths.get(targetFile), StandardCopyOption.REPLACE_EXISTING);
						log.info("No. of Rows updated in File Upload table :"+res);
					} catch (Exception e) {
						log.error("Exception in Workday Scheduler:"+e.getMessage());
					}
			
			}
			}
			
		}
		processFileDataIntoWDTables();
		log.info("Scheduler for Workday Inbound :: End");
	}
	
	public void processFileDataIntoWDTables() {
		List<FileUploadData> fileUploadDataList = integrationService.getFileUploadDetailsFromUploadTable();
		if (!fileUploadDataList.isEmpty()) {
			for (FileUploadData fileUploadData : fileUploadDataList) {
				if(fileUploadData.getFileName().contains("WD_BHECS")) {
					UploadResponseDTO response = integrationService.putDocumentOnPostgres(fileUploadData.getFileId() + "",
						fileUploadData.getId() + "", fileUploadData.getFileName(), fileUploadData.getCsvFileLocation(),fileUploadData.getId(),fileUploadData.getArchiveLocation());
				log.info("Data inserted to Staging Tables: {}",response);	
				
			}
			}
			//Process changed ids/ desc to URM/Application tables
			log.info("Process Workday Sub Business and Business Segment Data from Staging Table to Application Tables::Start");
			String messageQC = integrationService.processWorkdayQCDataToApplicationTables();
			log.info("Process Workday Sub Business and Business Segment Data from Staging Table to Application Tables::End",messageQC);
			
			//Process Workday Staging data to Application tables
			log.info("Process Workday Data from Staging Table to Application Tables::Start");
			String messageWDHR = integrationService.processWorkdayDataToApplicationTables();
			log.info("Process Workday Data from Staging Table to Application Tables::End",messageWDHR);
		}
	}
	
	
	@Scheduled(cron = "0 0 0 * * *",zone="CST")//Every day at 12am of CST--PROD
	public void executeHRDataLakeOutbound() {
		integrationService.getHRDataLakeOutboundData();
		log.info("HrDataLake Outbound is processed");
	}
	
	@Scheduled(cron = "0 0 0 * * *",zone="CST")//Every day at 12am of CST--PROD
	public void executeWorkdayOutboundOG() {	
		integrationService.getWorkdayOutboundData();
		log.info("Workday Outbound OG is processed");
	}
	
	@Scheduled(cron = "0 0 4 * * *",zone="CST")//Every day at 4AM of CST
	public void executeFullFileCostingReportDaily() {	
	    integrationService.getCostingReportDaily();
		log.info("Scheduler for Daily Costing Report is completed");
	}
	
	@Scheduled(cron = "0 0 0 * * *",zone="CST")//Every day at 12AM of CST--PROD
	public void executeWorkdayOutboundBH(){	
		integrationService.getWorkdayOutboundDataBH();
		log.info("Workday Outbound BH Daily is processed ");
	}
	
	@Scheduled(cron = "0 0 22 * * *",zone="CST")//Every day at 10PM of CST--PROD
	public void executeAutoApproveCostingBasedOnCutoffDate() {	
		String message = integrationService.executeAutoApproveCostingBasedOnCutoffDate();
		log.info("Auto Approved for costing based on Cut off date ",message);
	}

	@Scheduled(cron="0 0 5 ? * MON",zone="CST")//Every Monday at 5am of CST
	//@Scheduled(cron = "0 0 5 * * *",zone="CST")//Every day at 5 am of CST
	public void executeWeeklyDigestMail() {
		integrationService.sendMail();
	}
}