-- Table: bhecms.bh_integration_path

-- DROP TABLE bhecms.bh_integration_path;

CREATE TABLE bhecms.bh_integration_path
(
    source_path character varying(1000) COLLATE pg_catalog."default",
    archive_path character varying(1000) COLLATE pg_catalog."default",
    is_active character(1) COLLATE pg_catalog."default",
    integration_type character varying(50) COLLATE pg_catalog."default"
);

	
-- Table: bhecms.bh_template

-- DROP TABLE bhecms.bh_template;

CREATE TABLE bhecms.bh_template
(
    file_name character varying(150) COLLATE pg_catalog."default",
    template_name character varying(150) COLLATE pg_catalog."default",
    is_active character(1) COLLATE pg_catalog."default"
);

	
-- Table: bhecms.file_col_ref

-- DROP TABLE bhecms.file_col_ref;

CREATE TABLE bhecms.file_col_ref
(
    file_id integer NOT NULL,
    file_column_index integer NOT NULL,
    data_type_postgres character varying(100) COLLATE pg_catalog."default" NOT NULL,
    input_header_text character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    db_column_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    create_dtm timestamp without time zone NOT NULL,
    update_dtm timestamp without time zone NOT NULL,
    created_by character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    updated_by character varying(1000) COLLATE pg_catalog."default" NOT NULL
);

	
-- Table: bhecms.file_master

-- DROP TABLE bhecms.file_master;

CREATE TABLE bhecms.file_master
(
    file_id serial primary key,
    file_master_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    file_master_desc character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    download_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    total_columns integer NOT NULL,
    db_table_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    active character(1) COLLATE pg_catalog."default" NOT NULL,
    create_dtm timestamp without time zone NOT NULL,
    update_dtm timestamp without time zone NOT NULL,
    created_by character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    updated_by character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    schema_name character(100) COLLATE pg_catalog."default" NOT NULL
);

	
-- Table: bhecms.file_upload

-- DROP TABLE bhecms.file_upload;

CREATE TABLE bhecms.file_upload
(
    upload_id serial primary key,
    file_id integer NOT NULL,
    file_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    csv_file_location character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    row_passed integer,
    row_failed integer,
    total_rows integer,
    user_comment text COLLATE pg_catalog."default",
    db_processed character(1) COLLATE pg_catalog."default",
    create_dtm timestamp without time zone NOT NULL,
    update_dtm timestamp without time zone NOT NULL,
    created_by character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    updated_by character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    file_format character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    archive_location character varying(1000) COLLATE pg_catalog."default"
);
	
-- Table: bhecms.logger

-- DROP TABLE bhecms.logger;

CREATE TABLE bhecms.logger
(
    upload_or_run_id integer NOT NULL,
    log text COLLATE pg_catalog."default",
    create_dtm timestamp without time zone NOT NULL
);


INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('Default', 'Default', 'Default', '0', 'Default', 'N', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');
	
INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('REFERENCE CODE', 'REFERENCE CODE', 'REFERENCE CODE', '12', 'bh_ecs_stg_drm_refcode', 'Y', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');
	
INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('FDL CODE', 'FDL CODE', 'FDL CODE', '19', 'bh_ecs_stg_fdl_mapping', 'Y', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');
	
INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('PROJECT CODE', 'PROJECT CODE', 'PROJECT CODE', '11', 'bh_ecs_stg_drm_projcode', 'Y', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');
	
INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('GLE CODE', 'GLE CODE', 'GLE CODE', '11', 'bh_ecs_stg_parent_le', 'Y', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');
	
INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('WORKDAY CODE', 'WORKDAY CODE', 'WORKDAY CODE', '55', 'bh_ecs_stg_wd_requests', 'Y', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');
	
INSERT INTO bhecms.file_master(
	file_master_name, file_master_desc, download_name, total_columns, db_table_name, active, create_dtm, update_dtm, created_by, updated_by, schema_name)
	VALUES ('UEI CODE', 'UEI CODE', 'UEI CODE', '14', 'bh_ecs_stg_drm_uei', 'Y', now(), now(), 'SYSTEM', 'SYSTEM', 'bhecms');

INSERT INTO bhecms.bh_integration_path(
	source_path, archive_path, is_active, integration_type)
	VALUES ('/opt/bhecs/drm/inbound', '/opt/bhecs/drm/inbound/archive', 'Y', 'INBOUND');
INSERT INTO bhecms.bh_integration_path(
	source_path, archive_path, is_active, integration_type)
	VALUES ('/opt/bhecs/fdl/inbound', '/opt/bhecs/fdl/inbound/archive', 'Y', 'INBOUND');
INSERT INTO bhecms.bh_integration_path(
	source_path, archive_path, is_active, integration_type)
	VALUES ('/opt/bhecs/workday/inbound/daily', '/opt/bhecs/workday/inbound/daily/archive', 'Y', 'INBOUND');
	INSERT INTO bhecms.bh_integration_path(
	source_path, archive_path, is_active, integration_type)
	VALUES ('/opt/bhecs/workday/inbound/weekly', '/opt/bhecs/workday/inbound/weekly/archive', 'Y', 'INBOUND');
INSERT INTO bhecms.bh_integration_path(
	source_path, archive_path, is_active, integration_type)
	VALUES ('/opt/bhecs/hrdatalake/outbound', '/opt/bhecs/hrdatalake/outbound', 'Y', 'OUTBOUND');
INSERT INTO bhecms.bh_integration_path(
	source_path, archive_path, is_active, integration_type)
	VALUES ('/opt/bhecs/workday/outbound', '/opt/bhecs/workday/outbound', 'Y', 'OUTBOUND');

INSERT INTO bhecms.bh_template(
	file_name, template_name, is_active)
	VALUES ('REFERENCE', 'REFERENCE CODE', 'Y');
INSERT INTO bhecms.bh_template(
	file_name, template_name, is_active)
	VALUES ('PROJECT', 'PROJECT CODE', 'Y');
INSERT INTO bhecms.bh_template(
	file_name, template_name, is_active)
	VALUES ('UEI', 'UEI CODE', 'Y');
INSERT INTO bhecms.bh_template(
	file_name, template_name, is_active)
	VALUES ('FDL', 'FDL CODE', 'Y');
INSERT INTO bhecms.bh_template(
	file_name, template_name, is_active)
	VALUES ('WD', 'WORKDAY CODE', 'Y');
INSERT INTO bhecms.bh_template(
	file_name, template_name, is_active)
	VALUES ('LE', 'GLE CODE', 'Y');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='REFERENCE CODE'),2,'VARCHAR','REFERENCE_ID','reference_code',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='REFERENCE CODE'),3,'VARCHAR','REFERENCE_DESCRIPTION','reference_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='REFERENCE CODE'),4,'VARCHAR','ASSOCIATED_COMPANY_CODE','company_code',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='REFERENCE CODE'),5,'VARCHAR','ASSOCIATED_LE','reference_le',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');


--DML SCRIPT FOR FDL CODE ---SOME COLUMN NAMES TO BE CHANGED

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),1,'VARCHAR','drm gold id','gold_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),2,'VARCHAR','local cost center','local_cost_center',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),3,'VARCHAR','local cost center description','local_cost_center_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),4,'VARCHAR','local me','local_me',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),5,'VARCHAR','local me description','local_me_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),6,'VARCHAR','source system','erp_system',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),7,'VARCHAR','es function','es_function_code',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),8,'VARCHAR','es function description','es_function_desc',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),9,'VARCHAR','es company','es_company_code',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),10,'VARCHAR','es company description','es_company_code_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),11,'VARCHAR','es product line','es_product_line',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),12,'VARCHAR','es product line description','es_product_line_desc',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='FDL CODE'),13,'VARCHAR','uni analytical id','uni_analytical_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');


---DML SCRIPT FOR GLE CODE

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='GLE CODE'),1,'VARCHAR','LE_ID','legal_entity',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='GLE CODE'),2,'VARCHAR','OWNER_LE_ID','parent_legal_entity',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

--DML SCRIPT FOR PROJECT CODE

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='PROJECT CODE'),2,'VARCHAR','NATURAL_NODE_NAME','project_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='PROJECT CODE'),3,'VARCHAR','DESCRIPTION','project_desc',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');


--DML SCRIPT FOR UEI CODE

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='UEI CODE'),1,'VARCHAR','NAME','uei_code',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='UEI CODE'),2,'VARCHAR','DESCRIPTION','uei_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='UEI CODE'),3,'VARCHAR','ASSOCIATED_BU','uei_product_company',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='UEI CODE'),4,'VARCHAR','ASSOCIATED_LB','uei_company_code',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='UEI CODE'),6,'VARCHAR','ASSOCIATED_LE','uei_le',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');


--DML SCRIPT FOR WORKDAY CODE


INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),1,'VARCHAR','Employee ID','wd_employee_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),2,'VARCHAR','SSO ID','sso_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),3,'VARCHAR','Company Gold ID','gold_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),4,'VARCHAR','Company ID','company_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),5,'VARCHAR','Company ID Description','company_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),6,'VARCHAR','Cost Center - ID','cost_center_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),7,'VARCHAR','Cost Center - Description','cost_center_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),8,'VARCHAR','Job Title / Position Title','job_title',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),9,'VARCHAR','Job Function','job_function',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),10,'VARCHAR','Business Segment ID','business_segment_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),11,'VARCHAR','Business Segment Description','business_segment',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),12,'VARCHAR','Sub - Business ID','sub_business_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),13,'VARCHAR','Sub - Business Description','sub_business',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),14,'VARCHAR','Business Organization - ID','organization_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),15,'VARCHAR','Business Organization - Description','organization_name',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),16,'VARCHAR','Supervisory Organization - ID','supervisory_organization_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),17,'VARCHAR','Supervisory Organization - Description','supervisory_organization_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),18,'VARCHAR','Location Country','location_country',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),19,'VARCHAR','Payroll Country','payroll_country',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),20,'VARCHAR','Currency','currency',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),21,'VARCHAR','Baker Workday Pay Group','baker_workday_pay_group',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),22,'VARCHAR','Request Status','event_type',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),23,'VARCHAR','Direct / Indirect','direct_indirect',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),24,'VARCHAR','Company Hierarchy','company_hierarchy',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');


INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),25,'VARCHAR','Location City','location_city',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),26,'VARCHAR','Manager Emp ID','manager_emp_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),27,'VARCHAR','Management Level','management_level',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),28,'VARCHAR','Pay Rate Type','pay_rate_type',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),29,'VARCHAR','Workday User Account Name(Baker Network ID)','baker_network_id',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),30,'VARCHAR','First Name','first_name',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),31,'VARCHAR','Last Name','last_name',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),32,'VARCHAR','Job Profile Description','job_profile_description',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),33,'TIMESTAMP','Hire Date','hire_date',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),34,'VARCHAR','Employee Email - Work','employee_email_work',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),35,'TIMESTAMP','Effective Date','effective_date',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),36,'VARCHAR','Worker Status','emp_status',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),37,'VARCHAR','Employee Type','employee_type',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');
	
INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),38,'VARCHAR','Current Workday Costing String','current_workday_costing_string',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),39,'VARCHAR','Job Family','job_family',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),40,'TIMESTAMP','Termination Date','termination_date',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),41,'VARCHAR','Terminated','is_terminated',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');

INSERT INTO bhecms.FILE_COL_REF(file_id,file_column_index,data_type_postgres,input_header_text,db_column_name,create_dtm,update_dtm,created_by,updated_by) VALUES
((SELECT FILE_ID FROM bhecms.FILE_MASTER where FILE_MASTER_NAME='WORKDAY CODE'),42,'VARCHAR','OnLeave','on_leave',current_timestamp,current_timestamp,'SYSTEM','SYSTEM');