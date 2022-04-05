package com.bh.ecs.integration.common.utility;

public class Queries {
	
    public static final String GET_INTEGRATION_PATHS = "select * from bhecms.bh_integration_path";
	
	public static final String GET_MATCHED_TEMPLATE_DATA = "SELECT * from bhecms.bh_template";
	
	public static final String INSERT_FILE_UPLOAD_DATA = "Insert into bhecms.file_upload (file_id,file_name,csv_file_location,db_processed,user_comment,create_dtm,created_by,update_dtm,updated_by,file_format,archive_location) values (?,?,?,?,?,now(),?,now(),?,?,?)";
	
	public static final String GET_FILE_TEMPLATE = "select file_id from bhecms.file_master where file_master_name=?";
	
	public static final String GET_FILE_UPLOAD_DETAILS = "SELECT upload_id,file_id,csv_file_location,file_name,archive_location FROM bhecms.file_upload where db_processed='N' order by 1 asc";
	
	public static final String GET_FILE_TEMPLATE_DATA = "SELECT distinct file_mast.db_table_name,file_mast.schema_name,file_col_ref.* FROM bhecms.file_col_ref file_col_ref, bhecms.file_master file_mast where file_col_ref.file_id = file_mast.file_id and file_mast.active='Y' and file_col_ref.file_id=";
	
	public static final String UPDATE_FILE_UPLOAD_DATA_ERR = "update bhecms.file_upload set TOTAL_ROWS=?,ROW_PASSED=?,ROW_FAILED=?, DB_PROCESSED='Y',UPDATED_BY='SYSTEM',UPDATE_DTM=now(),ARCHIVE_LOCATION=? where upload_id=? and file_id=?";
	
	public static final String UPDATE_FILE_UPLOAD_DATA = "update bhecms.file_upload set DB_PROCESSED='Y',UPDATED_BY='1',UPDATE_DTM=now() where upload_id=? and file_id=?";
	
	public static final String FIND_BY_ERR_STATUS = "select * from bhecms.bh_ecs_stg_mass_upload_hist where status=?";
	
	public static final String FIND_BY_ERR_STATUS_FROM_MASS_UPLOAD = "select * from bhecms.bh_ecs_stg_mass_upload_hist where status in (?,?)";
	
	public static final String GET_EMP_DETAILS_FOR_EMAIL = "select distinct upper(urm.emp_name) emp_name,upper(urm.wd_employee_id) wd_employee_id,upper(emp.employee_email_work) employee_email_work from bhecms.bh_ecs_user_role_mapping urm,bhecms.bh_ecs_emp_details emp where emp.wd_employee_id = urm.wd_employee_id and urm.active_flag = 'Y' and urm.role_id = '40' and emp.employee_email_work is not null order by wd_employee_id";
	
	private Queries() {
		
	}
}