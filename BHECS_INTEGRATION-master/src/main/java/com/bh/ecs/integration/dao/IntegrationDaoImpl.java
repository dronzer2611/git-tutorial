package com.bh.ecs.integration.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import com.bh.ecs.integration.common.utility.Constants;
import com.bh.ecs.integration.common.utility.Queries;
import com.bh.ecs.integration.dto.CostingReport;
import com.bh.ecs.integration.dto.EmailContentDto;
import com.bh.ecs.integration.dto.EmpDetails;
import com.bh.ecs.integration.dto.FilePath;
import com.bh.ecs.integration.dto.TemplateDto;
import com.bh.ecs.integration.dto.WorkdayData;
import com.bh.ecs.integration.entity.FileUploadData;


@Repository
public class IntegrationDaoImpl implements IntegrationDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private Logger log = LoggerFactory.getLogger(IntegrationDaoImpl.class);
	
	@Override
	public List<FilePath> getIntegrationPath(){
		log.info("IntegrationDaoImpl-->getIntegrationPaths : Start");
		return jdbcTemplate.query(Queries.GET_INTEGRATION_PATHS,new ResultSetExtractor<List<FilePath>>() {
			@Override 
			public List<FilePath> extractData(ResultSet rs) throws SQLException {
		    	  List<FilePath> li= new ArrayList<>();
		    	  FilePath filePath;
		    	  
		    	 while(rs.next()){  
		    		 filePath = new FilePath();
		    		 filePath.setSourcePath(rs.getString("source_path"));
		    		 filePath.setArchivePath(rs.getString("archive_path"));
		    		 filePath.setIntegrationType(rs.getString("integration_type"));
				     li.add(filePath);
			        }
		    	    log.info("IntegrationDaoImpl-->getIntegrationPaths : End");
			        return li;
		      }
	    });
	}
	
	@Override
	public List<TemplateDto> getMatchedTemplateDetailsByFileName() {

		return jdbcTemplate.query(Queries.GET_MATCHED_TEMPLATE_DATA,new ResultSetExtractor<List<TemplateDto>>() {
			@Override 
			public List<TemplateDto> extractData(ResultSet rs) throws SQLException {
		    	  List<TemplateDto> li= new ArrayList<>();
		    	  TemplateDto templateDto;
		    	  
		    	 while(rs.next()){  
		    		 templateDto = new TemplateDto();
		    		 templateDto.setFileName(rs.getString("file_name"));
		    		 templateDto.setTemplateName(rs.getString("template_name"));
				     li.add(templateDto);
			        }
		    	    log.info("IntegrationDaoImpl-->getMatchedTemplateDetails : End");
			        return li;
		      }
	    });
	}
	
	@Override
	public int insertFileDetailsIntoUploadTable(String templateName, String fileName, String filePath, String userComments,String sso,String fileFormat, String archivedLocation) {
		log.info("IntegrationDaoImpl-->insertFileUploadData : Start");
		int row = 0;
		int tempId = 0;
		String dbProcessed = Constants.RES_FLAG_N;
		String targetLocation = archivedLocation;
		try {
			tempId = getFileTemplateID(templateName);
			if(templateName == null) {
				dbProcessed = Constants.RES_FLAG_Y;
				tempId = 1;
				int lastDot = fileName.lastIndexOf('.');
			    String targetFileName = fileName.substring(0,lastDot) + "_"+ getCurrentLocalTime().replace(":", "_").replace(".", "_")+ fileName.substring(lastDot);
				targetLocation = archivedLocation+Constants.SLASH+targetFileName;
			    Path targetPath = Paths.get(archivedLocation+Constants.SLASH+targetFileName);
				Path sourcePath = Paths.get(filePath);
				Files.move(sourcePath,targetPath);}
			row = jdbcTemplate.update(Queries.INSERT_FILE_UPLOAD_DATA,new Object[] { tempId, fileName, filePath,dbProcessed, userComments, sso, sso,fileFormat,targetLocation });
			
		} catch (Exception e) {
			log.error("IntegrationDaoImpl-->insertFileUploadData : Error - " ,e);
		}
		log.info("IntegrationDaoImpl-->insertFileUploadData : End");
		return row;
	}

	public int getFileTemplateID(String templateName) {
		log.info("IntegrationDaoImpl-->getFileTemplateID : Start");
		return jdbcTemplate.query(Queries.GET_FILE_TEMPLATE, new Object[] { templateName },new ResultSetExtractor<Integer>() {
                   @Override
					public Integer extractData(ResultSet rs) throws SQLException {
						int tempID = 0;
						while (rs.next()) {
							tempID = rs.getInt("file_id");
						}
						log.info("IntegrationDaoImpl-->getFileTemplateID : End");
						return tempID;
					}
				});

	}
	
	@Override
	public List<FileUploadData> getFileUploadDetailsFromUploadTable() {
		
		log.info("IntegrationDaoImpl-->getFileUploadDetails : Start");
		return jdbcTemplate.query(Queries.GET_FILE_UPLOAD_DETAILS, new ResultSetExtractor<List<FileUploadData>>() {
           @Override
			public List<FileUploadData> extractData(ResultSet rs) throws SQLException {
				List<FileUploadData> li = new ArrayList<>();
				FileUploadData fileUploadData;

				while (rs.next()) {
					fileUploadData = new FileUploadData();
					fileUploadData.setFileName(rs.getString("file_name"));
					fileUploadData.setCsvFileLocation(rs.getString("csv_file_location"));
					fileUploadData.setId(rs.getInt("upload_id"));
					fileUploadData.setFileId(rs.getInt("file_id"));
					fileUploadData.setArchiveLocation(rs.getString("archive_location"));
					li.add(fileUploadData);
				}
				log.info("IntegrationDaoImpl-->getFileUploadDetails : End");
				return li;
			}
		});

	}

	@Override
	public List<Map<String, String>> getTemplateDetailsFromFileMasterTable(String templateId) {
		return jdbcTemplate.query(Queries.GET_FILE_TEMPLATE_DATA + templateId,
				new ResultSetExtractor<List<Map<String, String>>>() {
                   @Override
					public List<Map<String, String>> extractData(ResultSet rs) throws SQLException {
						Map<String, String> hmapColumn = new HashMap<>();
						Map<String, String> hmapType = new HashMap<>();
						Map<String, String> fileMastTbNM = new HashMap<>();
						Map<String, String> fileMastSchema = new HashMap<>();
						List<Map<String, String>> li = new ArrayList<>();

						while (rs.next()) {
							hmapColumn.put(rs.getString("input_header_text").trim(),rs.getString("db_column_name").trim());
							hmapType.put(rs.getString("input_header_text").trim(),rs.getString("data_type_postgres").trim());
							fileMastTbNM.put("table_name", rs.getString("db_table_name"));
							fileMastSchema.put("schema_name", rs.getString("schema_name"));
						}
						li.add(hmapColumn);
						li.add(hmapType);
						li.add(fileMastTbNM);
						li.add(fileMastSchema);
						return li;
					}
				});
	}
	@Override
	public Map<String, String> insertFileDataIntoStagingTable(List<String> list) {
		log.info("IntegrationDaoImpl-->insertData : Start");
		Map<String, String> hmap = new HashMap<>();
		StringBuilder errorRowIds = new StringBuilder();
		int succRowCount = 0;
		int rowCount = 0;

		int totRowCount = list.size();
		try {
			Iterator<String> itr = list.iterator();
			int row ;
			while (itr.hasNext()) {
				rowCount++;
					String subQuery = itr.next();
					if (!"".equals(subQuery)) {
						row = jdbcTemplate.update(subQuery);
						if (row > 0) {
							succRowCount++;
						} else {
							errorRowIds.append("Error at Row :"+"[" + rowCount + ",0]-" + Constants.ERR_INSERT_CODE + "\n");
						}
					}
			}
		} catch (Exception e) {
			log.error("IntegrationDaoImpl-->insertData : Error - ",e);
			 errorRowIds.append("Error at Row :"+"[" + rowCount + ",0]-" + Constants.ERR_INSERT_CODE + "\n");
		}
		hmap.put("totalRowCount", totRowCount + "");
		hmap.put("successRowCount", succRowCount + "");
		hmap.put("failedRowCount", list.size() - succRowCount + "");
		if (succRowCount != totRowCount)
			hmap.put("errorLog", errorRowIds.toString());
		else 
			hmap.put("errorLog", "File Processed Successfully");
		hmap.put("isException", Constants.RES_FLAG_N);
		log.info("IntegrationDaoImpl-->updateRowCount : End");
		return hmap;
	}
	
	@Override
	public int updateFileUploadToUploadTable(String uploadID, String fileId, Map<String, String> errResLogMap, String targetFileLocation) {
		log.info("IntegrationDaoImpl-->updateFileUploadData : Start");
		int row = 0;
		try {
			if (errResLogMap.get("isException").equals(Constants.RES_FLAG_N)) {
				row = jdbcTemplate.update(Queries.UPDATE_FILE_UPLOAD_DATA_ERR,
						new Object[] { Integer.parseInt(errResLogMap.get("totalRowCount")),
								Integer.parseInt(errResLogMap.get("successRowCount")),
								Integer.parseInt(errResLogMap.get("failedRowCount")),targetFileLocation ,Integer.parseInt(uploadID),
								Integer.parseInt(fileId) });
			} else {
				row = jdbcTemplate.update(Queries.UPDATE_FILE_UPLOAD_DATA,
						new Object[] { Integer.parseInt(uploadID), Integer.parseInt(fileId) });
			}
		} catch (Exception e) {
			log.error("IntegrationDaoImpl-->updateFileUploadData : Error - " ,e);
		}
		log.info("IntegrationDaoImpl-->updateFileUploadData : End");
		return row;
	}
	
	@Override
	public int insertFileUploadLoggerData(int uploadId, String errLog) {
		log.info("IntegrationDaoImpl-->insertFileUploadLoggerData : Start");
		int row = 0;
		try {
			String query = "Insert into bhecms.logger (upload_or_run_id,log,create_dtm) values (?,?,now())";
			row = jdbcTemplate.update(query, new Object[] { uploadId, errLog });
		} catch (Exception e) {
			log.error("IntegrationDaoImpl-->insertFileUploadLoggerData : Error - " ,e);
		}
		log.info("IntegrationDaoImpl-->insertFileUploadLoggerData : End");
		return row;
	}
	

	@Override
	public String processReferenceCodeToRefMasterTable() {
     String message = null;
		try(Connection con = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmt = con.prepareCall("{? = call bhecms.insert_into_master_referencecode()}")) {
			callableStmt.registerOutParameter(1, Types.VARCHAR);
			callableStmt.execute();
			message = callableStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processReferenceCodeToMasterTable: {}",e);
		}
		return message;
	}
	
	@Override
	public String processGolnetCodeToGLNMasterTable() {
     String message = null;
		try(Connection con = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmt = con.prepareCall("{? = call bhecms.insert_into_master_parent_le()}")) {
			callableStmt.registerOutParameter(1, Types.VARCHAR);
			callableStmt.execute();
			message = callableStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processGolnetCodeToGLNMasterTable: {}",e);
		}
		return message;
	}

	
	@Override
	public String processFDLCodeToFDLMasterTable() {
     String message = null;
		try(Connection con = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmt = con.prepareCall("{? = call bhecms.insert_into_master_fdl_mapping()}")) {
			callableStmt.registerOutParameter(1, Types.VARCHAR);
			callableStmt.execute();
			message = callableStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processFDLCodeToFDLMasterTable: {}",e);
		}
		return message;
	}
	@Override
	public String updateGoldIds() {
     String message = null;
		try(Connection cont = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmnt = cont.prepareCall("{? = call bhecms.gold_id_update()}")) {
			callableStmnt.registerOutParameter(1, Types.VARCHAR);
			callableStmnt.execute();
			message = callableStmnt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in Update Gold Ids based on FDL Mapping and GLE Mapping: {}",e);
		}
		return message;
	}

	@Override
	public String processProjectCodeDataToPRMAsterTable() {
     String message = null;
		try(Connection con = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmt = con.prepareCall("{? = call bhecms.insert_into_master_projcode()}")) {
			callableStmt.registerOutParameter(1, Types.VARCHAR);
			callableStmt.execute();
			message = callableStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processProjectCodeDataToPRMAsterTable: {}",e);
		}
		return message;
	}

	@Override
	public String processUEICodeDataToMasterTable() {
     String message = null;
		try(Connection con = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmt = con.prepareCall("{? = call bhecms.insert_into_master_drm_uei()}")) {
			callableStmt.registerOutParameter(1, Types.VARCHAR);
			callableStmt.execute();
			message = callableStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processUEICodeDataToMasterTable: {}",e);
		}
		return message;
	}

	
	@Override
	public String processWorkdayDataToApplicationTables(){
		String message = null;
		try (Connection con = jdbcTemplate.getDataSource().getConnection();
				CallableStatement callableStmt = con.prepareCall("{? = call bhecms.request_insert()}")){
			callableStmt.registerOutParameter(1, Types.VARCHAR);
			callableStmt.execute();
			message = callableStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processWorkdayDataToApplicationTables: {}",e);
		} 
		return message;
	}
	@Override
	public String processWorkdayQCDataToApplicationTables() {
		String message = null;
		try(Connection cnct = jdbcTemplate.getDataSource().getConnection();
			CallableStatement clblstmt = cnct.prepareCall("{? = call bhecms.insert_business_segment_sub_bus_urm()}")){
			clblstmt.registerOutParameter(1, Types.VARCHAR);
			clblstmt.execute();
			message = clblstmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in processWorkdayQCDataToApplicationTables: {}",e);
		} 
		return message;
	}
	@Override
	public List<EmpDetails> getHRDataLake(){
		List<EmpDetails> list = new ArrayList<>();
		
		String sql = "select * from bhecms.bh_ecs_hrdl_outbound()";
		try(Connection connection =  jdbcTemplate.getDataSource().getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql);) {
			try (ResultSet rs = stmt.executeQuery()){
				while(rs.next()) {
				EmpDetails empDetails = new EmpDetails();
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("CST"));
				String strDate = format.format(new Date());
				empDetails.setEmployeeId(rs.getString(Constants.EMP_ID));
				empDetails.setEffectiveDate(rs.getString(Constants.EFF_DT));
				empDetails.setLegacyCostCenter(rs.getString(Constants.COST_CTR));
				empDetails.setLegacyCostCenterDesc(rs.getString("cost_center_description"));
				empDetails.setLaborCd(rs.getString("direct_indirect"));
				empDetails.setConcurGroup(rs.getString("concur_group"));
				empDetails.setBaseVar(rs.getString("cost_type"));
				empDetails.setLastUpdatedBy(rs.getString("last_updated_by"));
				empDetails.setLastUpdatedDt(rs.getString("last_updated_date"));
				empDetails.setOlCostCenter(rs.getString("ol_cost_center"));
				empDetails.setOlProjectCd(rs.getString("project_code"));
				empDetails.setOlRefCd(rs.getString("reference_code"));
				empDetails.setOledgerAdn(rs.getString("legacy_adn"));
				empDetails.setOlCompanyCode(rs.getString(Constants.CO_CO));
				empDetails.setOlProductLine(rs.getString("product_line"));
				empDetails.setCostingString(rs.getString("proposed_costing_string"));
				empDetails.setHfmFunctionCode(rs.getString("function_code"));
				empDetails.setRechargeFlag(rs.getString("rechrg_flag"));
				empDetails.setSellerUei(rs.getString("seller_uei"));
				empDetails.setSellerUeiDesc(rs.getString("seller_uei_desc"));
				empDetails.setSellerCostingAnalyst(rs.getString("seller_costing_analyst"));
				empDetails.setRechargeStartDate(rs.getString("rechrg_start_dt"));
				empDetails.setRechargeEndDate(rs.getString("rechrg_end_dt"));
				empDetails.setReceivingCompanyName(rs.getString("recvng_co_nm"));
				empDetails.setReceivingSubBusinessDesc(rs.getString("recvng_sub_busn_desc"));
				empDetails.setRechargeFrequency(rs.getString("rechrg_freqncy"));
				empDetails.setReceivingCostingAnalystNm(rs.getString("recvng_costing_analyst_nm"));
				empDetails.setSellerRechargeUpdatedBy(rs.getString("seller_rechrg_updated_by"));
				empDetails.setSellerRechargeLstUpdatedDate(rs.getString("seller_rechrg_last_updated_dt"));
				empDetails.setReceivingUei(rs.getString("recvng_uei"));
				empDetails.setReceivingUeiDesc(rs.getString("recvng_uei_desc"));
				empDetails.setReceivingCompCode(rs.getString("recvng_co_cd"));
				empDetails.setReceivingLegalEntityDt(rs.getString("recvng_legal_entity_dt"));
				empDetails.setReceivingLedgerCostCenter(rs.getString("recvng_ledger_cost_ctr"));
				empDetails.setReceivingMe(rs.getString("recvng_me"));
				empDetails.setReceivingGlAccount(rs.getString("recvng_gl_acct"));
				empDetails.setReceivingProductLine(rs.getString("recvng_pl"));
				empDetails.setReceivingProjectCode(rs.getString("recvng_project_cd"));
				empDetails.setReceivingRechargeLastUpdatedDate(rs.getString("recvng_rechrg_last_updated_dt"));
				empDetails.setReceivingRechargeLastUpdatedBy(rs.getString("recvng_rechrg_last_updated_by"));
				empDetails.setRechargeApproverUei(rs.getString("rechrg_apprvr_uei"));
				empDetails.setRechargeApproverUeiDesc(rs.getString("rechrg_apprvr_uei_desc"));
				empDetails.setRechargeApproverCompCode(rs.getString("rechrg_apprvr_co_cd"));
				empDetails.setRechargeApproverLegalEntityDt(rs.getString("rechrg_apprvr_legal_entity_dt"));
				empDetails.setRechargeApproverCostCtr(rs.getString("rechrg_apprvr_cost_ctr"));
				empDetails.setRechargeApproverMe(rs.getString("rechrg_apprvr_me"));
				empDetails.setRechargeApproverGLAcc(rs.getString("rechrg_apprvr_gl_acct"));
				empDetails.setRechargeApproverProductLine(rs.getString("rechrg_apprvr_pl"));
				empDetails.setRechargeApproverProjectCode(rs.getString("rechrg_apprvr_project_cd"));
				empDetails.setRechargeApproverNm(rs.getString("rechrg_apprvr_nm"));
				empDetails.setRechargeApproverUpdatedDt(rs.getString("rechrg_apprvr_updated_dt"));
				empDetails.setRunDate(strDate);
				empDetails.setBusinessRegion(rs.getString("business_region"));
				list.add(empDetails);
			}
			}
		} catch (SQLException e) {
			log.error("Exception in process getHrData() :"+e);
		}

		return list;
	}
	@Override
	public List<WorkdayData> getWorkdayOutboundData() {
		List<WorkdayData> list = new ArrayList<>();
		String sql = "select * from bhecms.bh_ecs_workday_outbound_geog()";
		try( Connection con = jdbcTemplate.getDataSource().getConnection();
			PreparedStatement stmnt = con.prepareStatement(sql)){
			try(ResultSet rslt = stmnt.executeQuery()){
			while(rslt.next()) {
				WorkdayData workdayData = new WorkdayData();
				workdayData.setEmpId(rslt.getString(Constants.EMP_ID));
				workdayData.setCostCenterId(rslt.getString("ol_cost_center"));
				workdayData.setCompanyCode(rslt.getString(Constants.CO_CO));
				workdayData.setLegacyCostCenter(rslt.getString(Constants.COST_CTR));
				workdayData.setProductLine(rslt.getString("product_line"));
				workdayData.setProjectCode(rslt.getString("project_code"));
				workdayData.setReferenceCode(rslt.getString("reference_code"));
				workdayData.setCostType(rslt.getString("cost_type"));
				workdayData.setDirectIndirect(rslt.getString("direct_indirect"));
				workdayData.setLedgerAdn(rslt.getString("legacy_adn"));
				workdayData.setEffectiveDate(rslt.getString(Constants.EFF_DT));
				list.add(workdayData);
			}
			}
		}catch (SQLException e) {
			log.error("Exception in process getWorkdayData() :"+e);
		}

		return list;
	}
	public static String getCurrentLocalTime() {
		LocalDateTime sDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_dd_yyyy_HH:mm:ss");
		return sDate.format(formatter);
	}
	@Override
	public List<CostingReport> getCostingReport(){
		List<CostingReport> list = new ArrayList<>();
		String sql = "select * from bhecms.approved_costing_report()";
		try (Connection conc = jdbcTemplate.getDataSource().getConnection();
			 PreparedStatement stmnt = conc.prepareStatement(sql)){
			
			try(ResultSet rst = stmnt.executeQuery()){
			while(rst.next()) {
				CostingReport costingReport = new CostingReport();
				costingReport.setEmployeeId(rst.getString("employeeid"));
                costingReport.setEmpName(rst.getString("empname"));
                costingReport.setSsoId(rst.getString("ssoid"));
                costingReport.setLocCountry(rst.getString("countrylocation"));
                costingReport.setBusinessTitle(rst.getString("businesstitle"));
                costingReport.setJobFamilyGroup(rst.getString("jobfamilygroup"));
                costingReport.setJobFamily(rst.getString("jobfamily"));
                costingReport.setOrganization(rst.getString("organizationname"));
                costingReport.setBusinessSegment(rst.getString("businesssegment"));
                costingReport.setSubBusiness(rst.getString("subbusiness"));
                costingReport.setWdCompanyId(rst.getString("companyid"));
                costingReport.setWdCostCenter(rst.getString("wdcostcenter"));
                costingReport.setWdRequestType(rst.getString("wdrequesttype"));
                costingReport.setCostingRequestType(rst.getString("costingrequesttype"));
                costingReport.setGoldId(rst.getString("goldid"));
                costingReport.setEffectiveDate(rst.getString("effectivedate"));
                costingReport.setClConcurGroup(rst.getString("clconcurgroup"));
                costingReport.setClDirectIndirect(rst.getString("cldirectindirect"));
                costingReport.setCurrentCostingString(rst.getString("currentcostingstring"));
                costingReport.setClCompanyCode(rst.getString("clcompanycode"));
                costingReport.setClCostCenter(rst.getString("clcostcenter"));
                costingReport.setClFunctionCode(rst.getString("clfunctioncode"));
                costingReport.setClProductLine(rst.getString("clproductline"));
                costingReport.setClProjectCode(rst.getString("clprojectcode"));
                costingReport.setClReferCode(rst.getString("clrefercode"));
                costingReport.setClCostType(rst.getString("clcosttype"));
                costingReport.setClLegacyAdn(rst.getString("cllegacyadn"));
                costingReport.setBusinessRegion(rst.getString("businessregion"));
                costingReport.setLastUpdatedDate(rst.getString("lastupdateddate"));
                costingReport.setLastUpdatedBy(rst.getString("lastupdatedby"));
                costingReport.setCaList(rst.getString("calist"));
                costingReport.setCostingRequestStatus(rst.getString("reqstatus"));
                costingReport.setRechargeapproved(rst.getString("rechargeapproved"));
				list.add(costingReport);
			}
			}
		}catch (SQLException e) {
			log.error("Exception in process getCostingReport() :"+e);
		}

		return list;
	}
	
	@Override
	public List<WorkdayData> getWorkdayOutboundDataBH() {
		List<WorkdayData> list = new ArrayList<>();
		String sql = "select * from bhecms.bh_ecs_workday_outbound_bhi()";
		try(Connection connn = jdbcTemplate.getDataSource().getConnection();
			PreparedStatement stmt = connn.prepareStatement(sql)) {
			try(ResultSet resultSt = stmt.executeQuery()){
			while(resultSt.next()) {
				WorkdayData workdayData = new WorkdayData();
				workdayData.setEmpId(resultSt.getString(Constants.EMP_ID));
				workdayData.setCompanyCode(resultSt.getString(Constants.CO_CO));
				workdayData.setLegacyCostCenter(resultSt.getString(Constants.COST_CTR));
				workdayData.setEffectiveDate(resultSt.getString(Constants.EFF_DT));
				list.add(workdayData);
			}
			}
		}catch (SQLException e) {
			log.error("Exception in process getWorkdayDataBH() :"+e);
		}

		return list;
	}
	@Override
	public String executeAutoApproveCostingBasedOnCutoffDate() {
		String message = null;
		try (Connection connect = jdbcTemplate.getDataSource().getConnection();
			 CallableStatement cllblStmt =  connect.prepareCall("{? = call bhecms.auto_approve_costing()}")){
			cllblStmt.registerOutParameter(1, Types.VARCHAR);
			cllblStmt.execute();
			message = cllblStmt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in executeAutoApproveCostingBasedOnCutoffDate: {}",e);
		} 
		return message;
	}
	@Override
	public List<EmailContentDto> getEmployeeDetails() {
		return jdbcTemplate.query(Queries.GET_EMP_DETAILS_FOR_EMAIL,new ResultSetExtractor<List<EmailContentDto>>() {
			@Override 
			public List<EmailContentDto> extractData(ResultSet rs) throws SQLException {
		    	  List<EmailContentDto> li= new ArrayList<>();
		    	  EmailContentDto emailDto;
		    	  
		    	 while(rs.next()){  
		    		 emailDto = new EmailContentDto();
		    		 emailDto.setEmployeeEmail(rs.getString("employee_email_work"));
		    		 emailDto.setEmployeeId(rs.getString("wd_employee_id"));
		    		 emailDto.setEmployeeName(rs.getString("emp_name"));
				     li.add(emailDto);
			        }
		    	    log.info("IntegrationDaoImpl-->getMatchedTemplateDetails : End");
			        return li;
		      }
	    });
	}

	@Override
	public List<EmailContentDto> getQueueCount(String employeeId) {
		List<EmailContentDto> list = new ArrayList<>();
		String query = "select * from bhecms.weekly_digest_queue_count(?)";
			 try (Connection conn = jdbcTemplate.getDataSource().getConnection();
		                PreparedStatement pstmt = conn.prepareStatement(query)) {
		            pstmt.setString(1,employeeId);
		            try(ResultSet rs = pstmt.executeQuery()){
		            while(rs.next()) {
		            	EmailContentDto workdayData = new EmailContentDto();
						workdayData.setValue(rs.getString("total"));
						workdayData.setRequestType(rs.getString("request_type"));
						list.add(workdayData);
		            }
		            }
		} catch (SQLException e) {
			log.error("Exception in getQueueCount: {}",e);
		} 
		return list;
	}
}
