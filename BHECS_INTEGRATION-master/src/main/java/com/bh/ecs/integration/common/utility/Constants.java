package com.bh.ecs.integration.common.utility;

import java.time.format.DateTimeFormatter;

public class Constants {
	public static final boolean PASSES = true;
	public static final boolean FAILS = false;
	public static final boolean SUCCESS = true;
	public static final boolean FAILURE = false;
	/** System property - <tt>line.separator</tt> */
	public static final String NEW_LINE = System.getProperty("line.separator");
	/** System property - <tt>file.separator</tt> */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/** System property - <tt>path.separator</tt> */
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");
	public static final String EMPTY_STRING = "";
	public static final String SPACE = " ";
	public static final String TAB = "\t";
	public static final String SINGLE_QUOTE = "'";
	public static final String PERIOD = ".";
	public static final String COMMA = ",";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String SLASH = "/";
	public static final String RES_FLAG_Y = "Y";
	public static final String RES_FLAG_N = "N";

	public static final String ERR_DATE_CODE = "100";
	public static final String ERR_INTG_CODE = "101";
	public static final String ERR_OTHR_CODE = "102";
	public static final String ERR_INSERT_CODE = "103";
	public static final String LOG_TYPE_CODE = "U";
	public static final String DB_SCHEMA = "bhecms.";
	public static final String SEPARATOR = "|";
	public static final String SUCCESS_MSG = "SUCCESS";
	public static final String WD_OUTBOUND = "BH_ECS_Costing_";
	public static final String ERROR_MAP = "errorLog";
	public static final String PASS_PHRASE_DEV = "bhecms@dev";
	public static final String PASS_PHRASE_QA = "bhecms@qa$";
	public static final String PASS_PHRASE_PROD = "bhecms@prod$";
	public static final DateTimeFormatter date = DateTimeFormatter.ofPattern("MM_dd_yyyy_HH_mm_ss");
	public static final String WORKDAY_COSTING_OUTBOUND_HEADER = "Employee ID|Filler|ES/HFM Company Code|ERP/Ledger CC|ES/HFM Product Line|ES/HFM Project Code|ES/HFM Reference Code|Cost Type|Indirect/Direct|Ledger ADN|Effective Date";
	public static final String HRDL_COSTING_OUTBOUND_HEADER = "emp_id,costing_eff_dt,legacy_cost_ctr,legacy_cost_ctr_desc,labor_cd,concur_group,base_var,last_updated_by,last_updated_dt,ol_cost_ctr,ol_project_cd,ol_ref_cd,ol_adn,ol_co_cd,ol_pl,costing_str,hfm_func_cd,erp_source,rechrg_flag,seller_uei,seller_uei_desc,seller_costing_analyst,rechrg_start_dt,rechrg_end_dt,recvng_co_nm,recvng_sub_busn_desc,rechrg_freqncy,recvng_costing_analyst_nm,seller_rechrg_updated_by,seller_rechrg_last_updated_dt,recvng_uei,recvng_uei_desc,recvng_co_cd,recvng_legal_entity_dt,recvng_ledger_cost_ctr,recvng_me,recvng_gl_acct,recvng_pl,recvng_project_cd,recvng_rechrg_last_updated_dt,recvng_rechrg_last_updated_by,rechrg_apprvr_uei,rechrg_apprvr_uei_desc,rechrg_apprvr_co_cd,rechrg_apprvr_legal_entity_dt,rechrg_apprvr_cost_ctr,rechrg_apprvr_me,rechrg_apprvr_gl_acct,rechrg_apprvr_pl,rechrg_apprvr_project_cd,rechrg_apprvr_nm,rechrg_apprvr_updated_dt,run_dt,busn_region_cntry";
	public static final String IN_PROGRESS = "IN PROGRESS";
	public static final String NOT_STARTED = "NOT STARTED";
	public static final DateTimeFormatter date_bh = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	public static final String WD_OUTBOUND_BH = "BHECS_BH_WD_Inbound_";
	public static final String WORKDAY_COSTING_OUTBOUND_BH_HEADER = "Employee ID|Workday Company code|ERP/Ledger Cost Center|Effective Date";
	public static final String COSTING_REPORT_OUTBOUND_HEADER = "Emp ID,Emp Name,SSO ID,Request Type,Payroll Legal Entity,Business Segment,Sub Business,Country,Business Title,Job Family Group,Job Family,Organization,WD Company Code,WD Cost Center,WD Transaction Type,Concur Group,Direct/Indirect,Costing String,Effective Date,ES/HFM Company Code,ERP/Ledger Cost Center,ES/HFM Function Code,ES/HFM Product Line,ES/HFM Project Code,ES/HFM Ref Code,Cost Type,Ledger ADN,Business Region,Request Status,List of Costing Analysts,Approved by Costing Analyst,Last Updated Date,Recharge Approved";
	public static final String ERROR_ROW = "Error at Row : ";
	public static final String ERROR_COL_NO = "and Column number : ";
	public static final String ERROR_COL_NM = "and Column Name : ";
	public static final String VCAP = "vcap.services.";
	public static final String OB = "OUTBOUND";
	public static final String EMP_ID = "wd_employee_id";
	public static final String EFF_DT = "effective_date";
	public static final String COST_CTR = "cost_center";
	public static final String CO_CO = "company_code";
	public static final String COSTING_REPORT_NAME = "BH_ECS_Costing_Report";
	public static final String HRDL_OB_FILE_NAME = "BH_ECS_HRDL-";
	public static final String COSTING_PATH_NAME = "costingreport";
	public static final String USNM = "svc-BHECS";
	public static final String PSSD = "8Fs}*_N4th6?V5W";
	public static final String HST = "smtphosthou.bakerhughes.com";
	public static final String BRK = " <br>";
	public static final String FRM = "svc-BHECS@bakerhughes.com";
	
	
	private Constants() {
		// this prevents even the native class from
		// calling this ctor as well :
		throw new AssertionError();
	}
}