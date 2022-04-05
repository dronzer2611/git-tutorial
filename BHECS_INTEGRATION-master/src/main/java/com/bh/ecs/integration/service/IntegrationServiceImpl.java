package com.bh.ecs.integration.service;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.bh.ecs.integration.common.utility.Constants;
import com.bh.ecs.integration.common.utility.Utility;
import com.bh.ecs.integration.controller.BatchProcessController;
import com.bh.ecs.integration.dao.IntegrationDao;
import com.bh.ecs.integration.dto.CostingReport;
import com.bh.ecs.integration.dto.EmpDetails;
import com.bh.ecs.integration.dto.ExtractFileDataDTO;
import com.bh.ecs.integration.dto.FilePath;
import com.bh.ecs.integration.dto.TemplateDto;
import com.bh.ecs.integration.dto.UploadResponseDTO;
import com.bh.ecs.integration.dto.WorkdayConfiguration;
import com.bh.ecs.integration.dto.WorkdayData;
import com.bh.ecs.integration.entity.FileUploadData;
import com.bh.ecs.integration.utils.CSVUtils;
import com.bh.ecs.integration.utils.KeyBasedFileProcessorUtil;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.bh.ecs.integration.dto.EmailContentDto;



@Service
public class IntegrationServiceImpl implements IntegrationService {
	
	private static final String TXT = ".txt";
	private static final String CSV = ".csv";

	private Logger log = LoggerFactory.getLogger(IntegrationServiceImpl.class);

	@Autowired
	IntegrationDao integrationDao;

	@Autowired
	CSVUtils csvUtils;

	@Autowired
	BatchProcessController batchController;
	
	@Autowired
	 ResourceLoader resourceLoader;
	
	@Autowired
    WorkdayConfiguration wdConfig;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Value("${emailUsername}")
	protected String emailUsername;
	
	@Value("${emailPassword}")
	protected String emailPassword;
	
	@Value("${emailFrom}")
	protected String from;
	
	@Value("${emailHost}")
	protected String host;
	
	@Override
	public List<FilePath> getIntegrationPath() {
		return integrationDao.getIntegrationPath();
	}
	
	@Override
	public String getMatchedTemplateDetailsByFileName(String fileName) {
	    
		List<TemplateDto> list = integrationDao.getMatchedTemplateDetailsByFileName();
		String templateName = null;
		for(TemplateDto temp:list) {
			if(fileName.toUpperCase().contains(temp.getFileName())) {
				templateName = temp.getTemplateName();
			}
		}
		
		log.info("Matched Template Method : ",templateName);
		return templateName;
	}
	@Override
	public int insertFileDetailsIntoUploadTable(String templateName, String fileName, String filePath, String userComments,String sso,String fileFormat, String archivedLocation) {
		return integrationDao.insertFileDetailsIntoUploadTable(templateName, fileName, filePath, userComments, sso,fileFormat,archivedLocation);
	}
	
	@Override
	public List<FileUploadData> getFileUploadDetailsFromUploadTable() {
		return integrationDao.getFileUploadDetailsFromUploadTable();
	}
	
	@Override
	public UploadResponseDTO putDocumentOnPostgres(String templateId, String uploadId, String fileName,String filePath,Integer uploadIdFrmTbl,String archiveLocation) {
		UploadResponseDTO response = new UploadResponseDTO();
		Path sourcePath = Paths.get(filePath);
        int lastDot = fileName.lastIndexOf('.');
        String targetFileName = fileName.substring(0,lastDot) + "_"+ getCurrentLocalTime().replace(":", "_").replace(".", "_")+"_"+uploadIdFrmTbl + fileName.substring(lastDot);
		Path targetPath = Paths.get(archiveLocation +Constants.SLASH+ targetFileName);
		String targetFileLocation = archiveLocation+Constants.SLASH+targetFileName;
		try(InputStream	stream = new FileInputStream(filePath)){
			List<Map<String, String>> li = getTemplateDetailsFromFileMasterTable(templateId);
			ExtractFileDataDTO extractFileDataDTO = extractCsvData(stream, li, uploadId,targetFileName);
			List<String> queryList = extractFileDataDTO.getFileData();
			Map<String, String> resMap = null;
			if (queryList != null && !queryList.isEmpty()) { 
				resMap = insertFileDataIntoStagingTable(queryList);
			} else {
				resMap = new HashMap<>();
				resMap.put("isException", Constants.RES_FLAG_Y);
				resMap.put(Constants.ERROR_MAP, extractFileDataDTO.getErrorLog());
			}

			resMap.put(Constants.ERROR_MAP, extractFileDataDTO.getErrorLog() + resMap.get(Constants.ERROR_MAP));
			updateFileUploadToUploadTable(uploadId, templateId, resMap,targetFileLocation);
			insertFileUploadLoggerData(Integer.parseInt(uploadId), resMap.get(Constants.ERROR_MAP));
			response.setStatus("File uploaded Successfully");
			Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			Set<PosixFilePermission> perms = new HashSet<>();
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
			Files.setPosixFilePermissions(targetPath, perms);
			log.info("Done with deleting");
		} catch (Exception e) {
			response.setStatus(e.getMessage());
			log.error("IntegrationServiceImpl-->putDocumentOnPostgres : Error - " ,e);
			try {
				
				Files.move(sourcePath,targetPath);
			} catch (IOException e1) {
				log.error("Problem in deleting files in source path::",e1);
			}
		}
		log.info("IntegrationServiceImpl-->putDocumentOnPostgres : End");
		return response;
	}

	@Override
	public List<Map<String, String>> getTemplateDetailsFromFileMasterTable(String templateId) {
		return integrationDao.getTemplateDetailsFromFileMasterTable(templateId);
	}

	public ExtractFileDataDTO extractCsvData(InputStream stream, List<Map<String, String>> templateDet, String uploadID, String targetFileName) {
		List<String> list = new ArrayList<>();
		ExtractFileDataDTO extractFileDataDTO = new ExtractFileDataDTO();
		try {
			List<String[]> allRows = csvUtils.readCSV(stream);
			Iterator<String[]> itrRow = allRows.iterator();
			String[] row = null;
			String[] headerRow = null;
			StringBuilder stgTableQuery;
			StringBuilder stgTableValues;
			Map<String, String> tempCol = templateDet.get(0);
			Map<String, String> tempColTyp = templateDet.get(1);
			Map<String, String> fileMastTblNm = templateDet.get(2);
			Map<String, String> fileMastSchema = templateDet.get(3);
			String tableName = fileMastTblNm.get("table_name");
			String tableSchema = fileMastSchema.get("schema_name");
			String headerColValRes = validateCsvHeaderColumns(tempColTyp, (String[]) allRows.get(0));
			StringBuilder sb = new StringBuilder();
			sb.append("File Name:"+targetFileName+"\n");
			sb.append("===============================\n");
			sb.append("Error Code Details:\n100 - Date field is having wrong data\n101 - Integer field is having wrong data\n102 - Field is having wrong data\n103 - Row insertion error (SQL Error) \n104 - System Error\n");
			sb.append("===============================\n");
			int i = 0;
			int rowCount = 1;
			int colCount = 0;
			Boolean numberExcp = false;
			Boolean dateExcp = false;
			Boolean otherExcp = false;
			int headerCount = 0;
			if (headerColValRes.equals(Constants.RES_FLAG_Y)) {

				while (itrRow.hasNext()) {
					if (i == 0) {
						headerRow = itrRow.next();
						headerCount = headerRow.length;
					}
					try {
						i = 1;
						if (numberExcp || dateExcp || otherExcp) {
							log.info("Executes nothing");
						} else {

							row = itrRow.next();
							for (int index = 0; index < row.length; index++) {
								row[index] = row[index].replaceAll("\"", "");
							}

						}
						stgTableQuery = new StringBuilder();
						stgTableValues = new StringBuilder();
						stgTableQuery.append("(");
						stgTableValues.append("(");
						if (numberExcp || dateExcp || otherExcp) {
							colCount++;
						} else {
							colCount = 0;
						}
						
						numberExcp = false;
						dateExcp = false;
						otherExcp = false;
						for (int counter = colCount; counter < row.length; counter++) {

							String colVal = row[counter];
							if (colCount < headerCount && (!"".equals(colVal.trim()) && tempColTyp.get(headerRow[colCount].trim()) != null)) {
									stgTableQuery.append(tempCol.get(headerRow[colCount].trim()) + ",");	
									  if("integer".equalsIgnoreCase(tempColTyp.get(headerRow[colCount].trim()))){
										  stgTableValues.append(Integer.parseInt(colVal.trim()) + ",");
									} else if("timestamp".equalsIgnoreCase(tempColTyp.get(headerRow[colCount].trim()))) {
										String value = Utility.convertToDate(colVal.trim()).toString();
										stgTableValues.append("'" + value.replaceAll("'", "''") + "',");
									} else {
										stgTableValues.append("'" + (colVal.trim()).replaceAll("'", "''") + "',");
									}

							}
							colCount++;
						}

						stgTableQuery.append("active_flag,created_by,created_date,last_updated_by,last_updated_date,process_status,upload_id)");
						stgTableValues.append("'Y','SYSTEM',now(),'SYSTEM',now(),'N',"+uploadID+")");
						if (list.size() < rowCount) {
							list.add("Insert into " + tableSchema + "." + tableName + stgTableQuery.toString() + " values "+ stgTableValues.toString());
						}
					} catch (NumberFormatException nfe) {
						numberExcp = true;
						if (list.size() < rowCount) {
							list.add("");
						}
						sb.append(Constants.ERROR_ROW + (rowCount + 1) + Constants.ERROR_COL_NO + (colCount + 1) + Constants.ERROR_COL_NM +(tempCol.get(headerRow[colCount].trim()))+ "-" + Constants.ERR_INTG_CODE + "\n");

					} catch (NullPointerException ne) {
						dateExcp = true;
						if (list.size() < rowCount) {
							list.add("");
						}
						sb.append(Constants.ERROR_ROW + (rowCount + 1) + Constants.ERROR_COL_NO + (colCount + 1) + Constants.ERROR_COL_NM +(tempCol.get(headerRow[colCount].trim()))+"-" + Constants.ERR_DATE_CODE + "\n");
						log.error("Null pointer Exception In extractCsvData() ",ne);

					} catch (Exception e) {
						otherExcp = true;
						if (list.size() < rowCount) {
							list.add("");
						}

						sb.append(Constants.ERROR_ROW + (rowCount + 1) + Constants.ERROR_COL_NO + (colCount + 1) + Constants.ERROR_COL_NM +(tempCol.get(headerRow[colCount].trim()))+"-" + Constants.ERR_OTHR_CODE + "\n");
						 log.error("In extractCsvData(.) : Exception : ",e);

					}
					if (numberExcp || dateExcp) {
						log.info("Executes nothing");
					} else {
						rowCount++;
					}
				}
				extractFileDataDTO.setErrorLog(sb.toString());
				
			} else {
				list = null;
				extractFileDataDTO.setErrorLog(headerColValRes);
				extractFileDataDTO.setStatus(Constants.RES_FLAG_N);
			}
		} catch (Exception e) {
			log.error("IntegrationServiceImpl-->extractCsvData : IOException : " ,e);
			list = null;
			extractFileDataDTO.setErrorLog("Exception at "+e.getMessage());
			extractFileDataDTO.setStatus("N");
		}
		extractFileDataDTO.setFileData(list);
		log.info("IntegrationServiceImpl-->extractCsvData : End");
		return extractFileDataDTO;
	}

	public String validateCsvHeaderColumns(Map<String, String> tempColTyp, String[] row) {
		log.info("IntegrationServiceImpl-->validateCsvHeaderColumns : Start");
		String valRes = "Y";
		try {

			int succCount = 0;
		    int temp; 
		    int totCount = tempColTyp.keySet().size();
			StringBuilder sb = new StringBuilder();
			sb.append("The Below Columns are not available\n");
			for (String dbColType : tempColTyp.keySet()) {
				temp = 0;
				for (String colVal : row) {
					if (dbColType.equalsIgnoreCase(colVal.trim())) {
						succCount++;
						temp = 1;
					}
				}
				if (temp == 0) {
					sb.append(dbColType + "\n");
				}
			}
			if (succCount != totCount) {
				valRes = sb.toString();
			} else if (totCount == 0) {
				valRes = "Template configuration is missing";
			}
		} catch (Exception e) {
			log.error("IntegrationServiceImpl-->validateCsvHeaderColumns : Exception : " , e);
			valRes = "Invalid Header Columns";
		}
		log.info("IntegrationServiceImpl-->validateCsvHeaderColumns : End");
		return valRes;

	}
	
	@Override
	public Map<String, String> insertFileDataIntoStagingTable(List<String> list) {
		return integrationDao.insertFileDataIntoStagingTable(list);
	}
	
	@Override
	public int updateFileUploadToUploadTable(String uploadID, String fileId, Map<String, String> errResLogMap,String targetFileLocation) {
		return integrationDao.updateFileUploadToUploadTable(uploadID, fileId, errResLogMap,targetFileLocation);

	}
	
	@Override
	public int insertFileUploadLoggerData(int uploadId, String errLog) {
		return integrationDao.insertFileUploadLoggerData(uploadId, errLog);

	}
	@Override
	public String processReferenceCodeToRefMasterTable() {
		log.info("IntegrationServiceImpl: processReferenceCodeToMasterTable::Start");
		String message = null;
		try {
			message = integrationDao.processReferenceCodeToRefMasterTable();
		} catch (Exception e) {
			log.error("Error in processReferenceCodeToMasterTable method::",e);
		}
		log.info("IntegrationServiceImpl: processReferenceCodeToMasterTable:: End");
		return message;
	}
	
	@Override
	public String processGolnetCodeToGLNMasterTable() {
		log.info("IntegrationServiceImpl: processGolnetCodeToGLNMasterTable::Start");
		String message = null;
		try {
			message = integrationDao.processGolnetCodeToGLNMasterTable();
		} catch (Exception e) {
			log.error("Error in processGolnetCodeToGLNMasterTable method::",e);
		}
		log.info("IntegrationServiceImpl: processGolnetCodeToGLNMasterTable:: End");
		return message;
	}
	@Override
	public String processFDLCodeToFDLMasterTable() {
       log.info("IntegrationServiceImpl: processFDLCodeToFDLMasterTable::Start");
		String message = null;
		try {
			message = integrationDao.processFDLCodeToFDLMasterTable();
		} catch (Exception e) {
			log.error("Error in processFDLCodeToFDLMasterTable method::",e);
		}
		log.info("IntegrationServiceImpl: processFDLCodeToFDLMasterTable:: End");
		return message;
	}
	@Override
	public String updateGoldIds() {
       log.info("IntegrationServiceImpl: Update Gold Ids based on FDL Mapping and GLE Mapping::Start");
		String message = null;
		try {
			message = integrationDao.updateGoldIds();
		} catch (Exception e) {
			log.error("Error in Update Gold Ids based on FDL Mapping and GLE Mapping method::",e);
		}
		log.info("IntegrationServiceImpl: Update Gold Ids based on FDL Mapping and GLE Mapping:: End");
		return message;
	}
	@Override
	public String processProjectCodeDataToPRMAsterTable() {
		String message = null;
      log.info("IntegrationServiceImpl: processProjectCodeDataToPRMAsterTable::Start");
		try {
			message = integrationDao.processProjectCodeDataToPRMAsterTable();
		} catch (Exception e) {
			log.error("Error in processProjectCodeDataToPRMAsterTable method::",e);
		}
		log.info("IntegrationServiceImpl: processProjectCodeDataToPRMAsterTable:: End");
		return message;
	}
	@Override
	public String processUEICodeDataToMasterTable(){
		String message = null;
		 log.info("IntegrationServiceImpl: processUEICodeDataToMasterTable::Start");
			try {
				message = integrationDao.processUEICodeDataToMasterTable();
			} catch (Exception e) {
				log.error("Error in processUEICodeDataToMasterTable method::",e);
			}
			log.info("IntegrationServiceImpl: processUEICodeDataToMasterTable:: End");
			return message;
	}
	@Override
	public String processWorkdayDataToApplicationTables(){
		String message = null;
		 log.info("IntegrationServiceImpl: processWorkdayDataToApplicationTables::Start");
			try {
				message = integrationDao.processWorkdayDataToApplicationTables();
			} catch (Exception e) {
				log.error("Error in processWorkdayDataToApplicationTables method::",e);
			}
			log.info("IntegrationServiceImpl: processWorkdayDataToApplicationTables:: End");
			return message;
	}
	@Override
	 public String processWorkdayQCDataToApplicationTables() {
		   String message = null;
			 log.info("IntegrationServiceImpl: processSubBusinessBusinessSegmentData::Start");
				try {
					message = integrationDao.processWorkdayQCDataToApplicationTables();
				} catch (Exception e) {
					log.error("Error in processWorkdayQCDataToApplicationTables method::",e);
				}
				log.info("IntegrationServiceImpl: processWorkdayQCDataToApplicationTables:: End");
				return message;
	   }
	
	@Override
	public void getHRDataLakeOutboundData() {
		List<FilePath> filePath= integrationDao.getIntegrationPath();
		for(FilePath file: filePath) {
			if(Constants.OB.equalsIgnoreCase(file.getIntegrationType()) && file.getSourcePath().contains("hrdatalake")) {
				List<EmpDetails> list = integrationDao.getHRDataLake();
				Path path;
				String fileName = file.getSourcePath() +Constants.SLASH+Constants.HRDL_OB_FILE_NAME
						+ getCurrentLocalTime().replace(":", "_").replace(".", "_") + CSV;
				path = Paths.get(fileName);
				if(!list.isEmpty()) {
					try(BufferedWriter writer = Files.newBufferedWriter(path,StandardCharsets.UTF_8)){
						writer.write(Constants.HRDL_COSTING_OUTBOUND_HEADER);
						String outStr = "";
						for(EmpDetails emp: list) {
							outStr = System.lineSeparator()+Constants.DOUBLE_QUOTE+ emp.getEmployeeId()+Constants.DOUBLE_QUOTE+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getEffectiveDate()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA +Constants.DOUBLE_QUOTE+ emp.getLegacyCostCenter()+Constants.DOUBLE_QUOTE+ Constants.COMMA+Constants.DOUBLE_QUOTE
									+ emp.getLegacyCostCenterDesc()+Constants.DOUBLE_QUOTE + Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getLaborCd()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA +Constants.DOUBLE_QUOTE+ emp.getConcurGroup()+ Constants.DOUBLE_QUOTE+ Constants.COMMA
									+ Constants.DOUBLE_QUOTE+ emp.getBaseVar()+Constants.DOUBLE_QUOTE+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getLastUpdatedBy()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA +Constants.DOUBLE_QUOTE+ emp.getLastUpdatedDt() +Constants.DOUBLE_QUOTE+ Constants.COMMA+ Constants.DOUBLE_QUOTE+emp.getOlCostCenter()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA+Constants.DOUBLE_QUOTE+emp.getOlProjectCd()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getOlRefCd()+Constants.DOUBLE_QUOTE+ Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getOledgerAdn()+Constants.DOUBLE_QUOTE + Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getOlCompanyCode()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getOlProductLine() +Constants.DOUBLE_QUOTE+ Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getCostingString() +Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getHfmFunctionCode()+Constants.DOUBLE_QUOTE+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getErpSource()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeFlag()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getSellerUei() +Constants.DOUBLE_QUOTE+ Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getSellerUeiDesc() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getSellerCostingAnalyst()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeStartDate() +Constants.DOUBLE_QUOTE+ Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getRechargeEndDate() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingCompanyName()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingSubBusinessDesc() + Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getRechargeFrequency() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingCostingAnalystNm()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getSellerRechargeUpdatedBy() + Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getSellerRechargeLstUpdatedDate() + Constants.DOUBLE_QUOTE+Constants.COMMA 
									+ Constants.DOUBLE_QUOTE+emp.getReceivingUei() + Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getReceivingUeiDesc() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingCompCode()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingLegalEntityDt() + Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getReceivingLedgerCostCenter() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingMe()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingGlAccount()+ Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getReceivingProductLine() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingProjectCode()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getReceivingRechargeLastUpdatedDate()+ Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getReceivingRechargeLastUpdatedBy() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverUei()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverUeiDesc()+ Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getRechargeApproverCompCode() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverLegalEntityDt()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverCostCtr()+ Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getRechargeApproverMe() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverGLAcc()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverProductLine()+ Constants.DOUBLE_QUOTE+Constants.COMMA
									+ Constants.DOUBLE_QUOTE+emp.getRechargeApproverProjectCode()+ Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRechargeApproverNm() +Constants.DOUBLE_QUOTE+Constants.COMMA+Constants.DOUBLE_QUOTE+emp.getRechargeApproverUpdatedDt()+Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getRunDate() +Constants.DOUBLE_QUOTE
									+ Constants.COMMA + Constants.DOUBLE_QUOTE+emp.getBusinessRegion() +Constants.DOUBLE_QUOTE;
									outStr = outStr.replaceAll("null", "");
									writer.write(outStr);

						}
						//to set permissions to access the file.
						Set<PosixFilePermission> permssion = new HashSet<>();
						permssion.add(PosixFilePermission.OWNER_READ);
						permssion.add(PosixFilePermission.OWNER_WRITE);
						permssion.add(PosixFilePermission.OWNER_EXECUTE);
						permssion.add(PosixFilePermission.GROUP_READ);
						permssion.add(PosixFilePermission.GROUP_EXECUTE);
						permssion.add(PosixFilePermission.OTHERS_READ);
						permssion.add(PosixFilePermission.OTHERS_EXECUTE);
						Files.setPosixFilePermissions(path, permssion);

						} catch (IOException e) {
							log.error("Exception :" +e);
							try {
							Files.deleteIfExists(path);
						} catch (IOException e1) {
							log.error("IO Exception in HRDATA():" + e1);
						}
						}
				}
			}
		}
		
	}
	
	public static String getCurrentLocalTime() {
		LocalDateTime sDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_dd_yyyy_HH:mm:ss");
		return sDate.format(formatter);
	}
	
	@Override
	public void getWorkdayOutboundData() {
		List<FilePath> filePaths= integrationDao.getIntegrationPath();	
		for(FilePath filePath : filePaths) {
			if(Constants.OB.equalsIgnoreCase(filePath.getIntegrationType()) && filePath.getSourcePath().contains("workday")) {
				List<WorkdayData> wdList;
				wdList = integrationDao.getWorkdayOutboundData();	
				if(!wdList.isEmpty()) {
				//List<List<WorkdayData>> list =nPartition(wdList,1000);
					//for (List<WorkdayData> subList : list) {
					//generateWDOBGEOGFile(wdList,filePath);
				//}
					File filetoARC;
					String strDate = LocalDateTime.now().format(Constants.date);
					try {
						String fileNameARC = filePath.getSourcePath() + Constants.SLASH + Constants.WD_OUTBOUND + strDate + TXT;
						String fileName = filePath.getSourcePath() + Constants.SLASH + Constants.WD_OUTBOUND + strDate+ TXT;
						filetoARC = new File(fileNameARC);
						writeToFile(filetoARC, wdList, fileName);
					}

					catch (Exception e) {
						log.error("error while executing runWorkdayOutboundJob::", e);
					}
			}
		}
		
		
	}
	
	}
	
	private File writeToFile(File file, List<WorkdayData> wdList,String fileCos) {
		try (FileWriter fos = new FileWriter(file, true);
				BufferedWriter out = new BufferedWriter(fos)){
			out.write(Constants.WORKDAY_COSTING_OUTBOUND_HEADER);
			String outStr = "";
			for (WorkdayData empWorkBound : wdList) {
				outStr = System.lineSeparator()+ empWorkBound.getEmpId() + Constants.SEPARATOR + empWorkBound.getCostCenterId()
				+ Constants.SEPARATOR + empWorkBound.getCompanyCode() + Constants.SEPARATOR
				+ empWorkBound.getLegacyCostCenter() + Constants.SEPARATOR + empWorkBound.getProductLine()
				+ Constants.SEPARATOR + empWorkBound.getProjectCode() + Constants.SEPARATOR
				+ empWorkBound.getReferenceCode() + Constants.SEPARATOR + empWorkBound.getCostType()
				+ Constants.SEPARATOR + empWorkBound.getDirectIndirect() + Constants.SEPARATOR
				+ empWorkBound.getLedgerAdn() + Constants.SEPARATOR + empWorkBound.getEffectiveDate();
				outStr = outStr.replaceAll("null", "");
				out.write(outStr);
			}	
			Set<PosixFilePermission> permss = new HashSet<>();
			permss.add(PosixFilePermission.OWNER_READ);
			permss.add(PosixFilePermission.OWNER_WRITE);
			permss.add(PosixFilePermission.OWNER_EXECUTE);
			permss.add(PosixFilePermission.GROUP_READ);
			permss.add(PosixFilePermission.GROUP_EXECUTE);
			permss.add(PosixFilePermission.OTHERS_READ);
			permss.add(PosixFilePermission.OTHERS_EXECUTE);
			Files.setPosixFilePermissions(Paths.get(fileCos), permss);
		} catch (Exception e) {
			try {
				file.deleteOnExit();
			} catch (Exception e1) {
				log.error("Error in closing the file"+e1);
			}
			log.error("error while executing writeToFile::", e);
		}
		return file;
	}
	
	private <T> List<List<T>> nPartition(List<T> objs, final int nValue) {
	    return new ArrayList<>(IntStream.range(0, objs.size()).boxed().collect(
	            Collectors.groupingBy(e->e/nValue,Collectors.mapping(e->objs.get(e), Collectors.toList())
	                    )).values());
	    }	
	@Override
	public String decryptJob(String inputFile,String outputFile) {
	        InputStream keyIn = null;
	        try (FileInputStream in = new FileInputStream(inputFile);
	        		FileOutputStream out = new FileOutputStream(outputFile)){
	            Resource resource = resourceLoader.getResource("classpath:" + wdConfig.getPgpPrivateKeyFileName());
	            keyIn = resource.getInputStream();
	            KeyBasedFileProcessorUtil.decryptFile(in, out, keyIn, Constants.PASS_PHRASE_PROD.toCharArray());
	            keyIn.close();
	        } catch (Exception e) {
	            log.error("Exception in decrypt job ",e);
	        }
	        return Constants.SUCCESS_MSG;
  }

	@Override
	 public void getCostingReportDaily() {
		   List<FilePath> filePths  = integrationDao.getIntegrationPath();
			for(FilePath filePth : filePths) {
				if(Constants.OB.equalsIgnoreCase(filePth.getIntegrationType()) && filePth.getSourcePath().contains(Constants.COSTING_PATH_NAME)) {
					List<CostingReport> list;
					list = integrationDao.getCostingReport();
					Path path ;
					String fileName = filePth.getSourcePath() +Constants.SLASH+Constants.COSTING_REPORT_NAME + CSV;
					path = Paths.get(fileName);
					if(!list.isEmpty()) {
						try(BufferedWriter writer = Files.newBufferedWriter(path,StandardCharsets.UTF_8)){
							writer.write(Constants.COSTING_REPORT_OUTBOUND_HEADER);
							String outStr = "";
							for(CostingReport costingReport: list) {
								outStr = System.lineSeparator()+Constants.DOUBLE_QUOTE+ costingReport.getEmployeeId()+Constants.DOUBLE_QUOTE+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getEmpName()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA +Constants.DOUBLE_QUOTE+ costingReport.getSsoId()+Constants.DOUBLE_QUOTE+ Constants.COMMA+Constants.DOUBLE_QUOTE
										+ costingReport.getCostingRequestType()+Constants.DOUBLE_QUOTE + Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getGoldId()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA +Constants.DOUBLE_QUOTE+ costingReport.getBusinessSegment()+ Constants.DOUBLE_QUOTE+ Constants.COMMA
										+ Constants.DOUBLE_QUOTE+ costingReport.getSubBusiness()+Constants.DOUBLE_QUOTE+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getLocCountry()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA +Constants.DOUBLE_QUOTE+ costingReport.getBusinessTitle() + Constants.DOUBLE_QUOTE 
										+ Constants.COMMA + Constants.DOUBLE_QUOTE + costingReport.getJobFamilyGroup() + Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE + costingReport.getJobFamily() + Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE + costingReport.getOrganization() + Constants.DOUBLE_QUOTE
										+ Constants.COMMA +Constants.DOUBLE_QUOTE+ costingReport.getWdCompanyId() +Constants.DOUBLE_QUOTE+ Constants.COMMA+ Constants.DOUBLE_QUOTE+costingReport.getWdCostCenter()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA+Constants.DOUBLE_QUOTE+costingReport.getWdRequestType()+Constants.DOUBLE_QUOTE + Constants.COMMA
										+ Constants.DOUBLE_QUOTE+costingReport.getClConcurGroup()+Constants.DOUBLE_QUOTE 
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClDirectIndirect()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getCurrentCostingString() +Constants.DOUBLE_QUOTE+ Constants.COMMA
										+ Constants.DOUBLE_QUOTE+costingReport.getEffectiveDate()+Constants.DOUBLE_QUOTE+ Constants.COMMA
										+ Constants.DOUBLE_QUOTE+costingReport.getClCompanyCode() +Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClCostCenter()+Constants.DOUBLE_QUOTE+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClFunctionCode()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClProductLine()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClProjectCode() +Constants.DOUBLE_QUOTE+ Constants.COMMA
										+ Constants.DOUBLE_QUOTE+costingReport.getClReferCode() + Constants.DOUBLE_QUOTE+Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClCostType()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getClLegacyAdn() +Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE+costingReport.getBusinessRegion() +Constants.DOUBLE_QUOTE
										+ Constants.COMMA + Constants.DOUBLE_QUOTE + costingReport.getCostingRequestStatus() + Constants.DOUBLE_QUOTE
										+ Constants.COMMA+Constants.DOUBLE_QUOTE+costingReport.getCaList()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA+Constants.DOUBLE_QUOTE+costingReport.getLastUpdatedBy()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA+Constants.DOUBLE_QUOTE+costingReport.getLastUpdatedDate()+Constants.DOUBLE_QUOTE
										+ Constants.COMMA+Constants.DOUBLE_QUOTE+costingReport.getRechargeapproved()+Constants.DOUBLE_QUOTE;
										outStr = outStr.replaceAll("null", "");
										writer.write(outStr);

							}
							//to set permissions to access the file.
							Set<PosixFilePermission> perm = new HashSet<>();
							perm.add(PosixFilePermission.OWNER_READ);
							perm.add(PosixFilePermission.OWNER_WRITE);
							perm.add(PosixFilePermission.OWNER_EXECUTE);
							perm.add(PosixFilePermission.GROUP_READ);
							perm.add(PosixFilePermission.GROUP_EXECUTE);
							perm.add(PosixFilePermission.OTHERS_READ);
							perm.add(PosixFilePermission.OTHERS_EXECUTE);
							Files.setPosixFilePermissions(path, perm);

							} catch (IOException e) {
								log.error("",e);
								try {
								Files.deleteIfExists(path);
							} catch (IOException e1) {
								log.error("IO Exception in getCostingReport():" , e1);
							}
							}
					}
				}
			}
	   }
	
	 @Override
		public void getWorkdayOutboundDataBH() {
			List<FilePath> flePaths= integrationDao.getIntegrationPath();
			for(FilePath flePath : flePaths) {
				if(Constants.OB.equalsIgnoreCase(flePath.getIntegrationType()) && flePath.getSourcePath().contains("workday")) {
					List<WorkdayData> wdList;
					wdList = integrationDao.getWorkdayOutboundDataBH();	
					if(!wdList.isEmpty()) {
					//List<List<WorkdayData>> list =nPartition(wdList,1000);
					//for (List<WorkdayData> subList : list) {
						//generateWDOBBHFile(wdList, flePath);
					//}
						 String strDate = LocalDateTime.now().format(Constants.date_bh);
						 File filetoARC;
							try {
								String fileNameARC = flePath.getSourcePath()+Constants.SLASH+Constants.WD_OUTBOUND_BH+strDate+TXT;
								String fileName = flePath.getSourcePath()+Constants.SLASH+Constants.WD_OUTBOUND_BH+strDate+TXT;
								filetoARC = new File(fileNameARC); 
								writeToFileBH(filetoARC, wdList,fileName);
							}
							
							catch (Exception e) {
								log.error("error while executing runWorkdayOutboundJob::", e);
							}
				}
			}
			
			
		}
		
		}
		private File writeToFileBH(File file, List<WorkdayData> wdList,String fileCos) {
			StringBuilder sb = new StringBuilder();
			try (FileWriter fos = new FileWriter(file, true);
					BufferedWriter out =  new BufferedWriter(fos)){
				out.write(Constants.WORKDAY_COSTING_OUTBOUND_BH_HEADER);
				String outStr = "";
				for (WorkdayData empWorkBound : wdList) {
					outStr = System.lineSeparator()+ empWorkBound.getEmpId() + Constants.SEPARATOR
							+ empWorkBound.getCompanyCode() + Constants.SEPARATOR
					+ empWorkBound.getLegacyCostCenter() + Constants.SEPARATOR  + empWorkBound.getEffectiveDate();
					outStr = outStr.replaceAll("null", "");
					out.write(outStr);
				}	
				Set<PosixFilePermission> permm = new HashSet<>();
				permm.add(PosixFilePermission.OWNER_READ);
				permm.add(PosixFilePermission.OWNER_WRITE);
				permm.add(PosixFilePermission.OWNER_EXECUTE);
				permm.add(PosixFilePermission.GROUP_READ);
				permm.add(PosixFilePermission.GROUP_EXECUTE);
				permm.add(PosixFilePermission.OTHERS_READ);
				permm.add(PosixFilePermission.OTHERS_EXECUTE);
				Files.setPosixFilePermissions(Paths.get(fileCos), permm);
			} catch (Exception e) {
				try {
					file.deleteOnExit();
					sb.append(e.getMessage());
				} catch (Exception e1) {
					log.error("Error in closing the file"+e1);
					sb.append(e.getMessage());
				}
				log.error("error while executing writeToFile::", e);
			}
			return file;
		}
		
		@Override
		 public String executeAutoApproveCostingBasedOnCutoffDate() {
			   return  integrationDao.executeAutoApproveCostingBasedOnCutoffDate(); 
		   }

	@Override
	public void sendMail() {
		List<EmailContentDto> empList = integrationDao.getEmployeeDetails();
		 for(EmailContentDto emailcontent : empList) {
		String empName = emailcontent.getEmployeeName();
		String to = emailcontent.getEmployeeEmail();
		List<EmailContentDto> queueCount = integrationDao.getQueueCount(emailcontent.getEmployeeId());
		LocalDateTime sDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd'" + getDayNumberSuffix(sDate.getDayOfMonth()) + "' MMMM yyyy");
		String strDate= sDate.format(formatter);
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");
		String hireCount = queueCount.get(0).getValue();
		String lifeCycleCount = queueCount.get(1).getValue();
		String proposedCount = queueCount.get(2).getValue();
		String rechargeCount = queueCount.get(3).getValue();
		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailUsername, emailPassword);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("BHECS Inbox - Your Weekly Digest");
	        String body = "<!DOCTYPE html>" + 
	        		"<head>" + 
	        		"<style>" + 
	        		"table, th, td {" + 
	        		"  border: 1px solid black;" + 
	        		"  border-collapse: collapse;" + 
	        		"}" + 
	        		"</style>" + 
	        		"</head>" + 
	        		" <body style ='font-family:poppins-regular'>" + 
	        		" <div style = 'text-align:center;color: #05322b;'><font size='5px'> Hi "+empName+"," + 
	        		"  here's your BHECS weekly digest" + 
	        		" <br><br>" + 
	        		 strDate+"</font>" + 
	        		" </div>" + 
	        		" <div style='float:left'>" + 
	        		" <br><br>Open items in your BHECS queue :  " + 
	        		" <br><br>" + 
	        		" <table style='width:30%'>" + 
	        		" <tr>" + 
	        		" <td>Hire </td>" + 
	        		" <td style='text-align:center'>"+hireCount+"</td>" + 
	        		" </tr>" + 
	        		" <tr>" + 
	        		" <td>Life Cycle </td>" + 
	        		" <td style='text-align:center'>"+lifeCycleCount+"</td>" + 
	        		" </tr>" + 
	        		" <tr>" + 
	        		" <td>Proposed Change </td>" + 
	        		" <td style='text-align:center'>"+ proposedCount+" </td>" + 
	        		" </tr>" + 
	        		" <tr>" + 
	        		" <td>Recharge </td>" + 
	        		" <td style='text-align:center'>"+rechargeCount+"</td>" + 
	        		" </tr>" + 
	        		" </table>" + 
	        		" <br>" + 
	        		" <br>" + 
	        		" <a href ='https://employee-cost.bakerhughes.com/'>Click here </a> to sign-in to BHECS" + 
	        		" <br><br>" + 
	        		" <div style='color: #05322b;'><font size='3px'>Please use VPN and Google Chrome to access BHECS Application</font></div>" + 
	        		" <br>" + 
	        		" For further assistance, please mail to <a href ='mailto:BHECS.App.Support@bakerhughes.com'>BHECS Support Team</a>" + 
	        		" <br><br>" + 
	        		" <b>Note: As this is a system-generated message from an unmonitored mail box, please do not reply to this message.</b>" + 
	        		" <br><br>Regards," + 
	        		" <br>" + 
	        		" BHECS Support Team</div>\r\n" + 
	        		" </body>" + 
	        		" </html>";
			// Send the complete message parts
			message.setContent(body, "text/html; charset=utf-8");
			message.saveChanges();
			Transport.send(message);
			log.info("Sent message Successfully");
		} catch (MessagingException e) {
			log.error("Exception in Weekly Email Digest", e);
		}
		 }
	}

	private String getDayNumberSuffix(int day) {
		if (day >= 11 && day <= 13) {
			return "th";
		}
		switch (day % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}
}
