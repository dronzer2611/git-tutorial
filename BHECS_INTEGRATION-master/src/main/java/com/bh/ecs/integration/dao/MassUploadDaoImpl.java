package com.bh.ecs.integration.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import com.bh.ecs.integration.common.utility.Queries;
import com.bh.ecs.integration.entity.BhEcsMassUploadHistory;

@Repository
public class MassUploadDaoImpl implements MassUploadDao{
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private Logger log = LoggerFactory.getLogger(IntegrationDaoImpl.class);


	@Override
	public String validateRecords(Integer loadNumber) {
		String str = null;
		
		try (Connection con = jdbcTemplate.getDataSource().getConnection();
			 CallableStatement callableStatement = con.prepareCall("{? = call bhecms.mass_upload_rule_check(?)}")){
			callableStatement.registerOutParameter(1,Types.VARCHAR);
			callableStatement.setInt(2,loadNumber);
			callableStatement.execute();
			str = callableStatement.getString(1);
		} catch (SQLException e) {
			log.error("Exception in loadNumber: {}",e);
		} 
		return str;
	}


	@Override
	public String approvedQueue(Integer loadNo) {
		String str = null;
		try(Connection conn = jdbcTemplate.getDataSource().getConnection();
			CallableStatement callableStatmnt = conn.prepareCall("{? = call bhecms.mass_upload(?)}")) {
			callableStatmnt.registerOutParameter(1,Types.VARCHAR);
			callableStatmnt.setInt(2,loadNo);
			callableStatmnt.execute();
			str = callableStatmnt.getString(1);
		} catch (SQLException e) {
			log.error("Exception in approvedQueue: {}",e);
		} 
		return str;
	}

	@Override
	public List<BhEcsMassUploadHistory> findByErrorStatus(String status){
		
		  log.info("MassUploadDaoImpl-->findByErrorStatus : Start");
			return jdbcTemplate.query(Queries.FIND_BY_ERR_STATUS, new Object[] { status },new ResultSetExtractor<List<BhEcsMassUploadHistory>>() {
				@Override 
				public List<BhEcsMassUploadHistory> extractData(ResultSet rs) throws SQLException {
			    	  List<BhEcsMassUploadHistory> li= new ArrayList<>();
			    	  BhEcsMassUploadHistory bhEcsMassUploadHis;
			    	 while(rs.next()){  
			    		 bhEcsMassUploadHis = new BhEcsMassUploadHistory();
			    		 bhEcsMassUploadHis.setLoadNumber(rs.getInt("load_number"));
			    		 bhEcsMassUploadHis.setWdEmployeeId(rs.getString("wd_employee_id"));
					     li.add(bhEcsMassUploadHis);
				        }
			    	    log.info("MassUploadDaoImpl-->findByErrorStatus : End");
				        return li;
			      }
		    });
	}
	@Override
	public List<BhEcsMassUploadHistory> findByErrStatusInMassUploadHistTbl(String nstrtStatus, String inprStatus){
		 log.info("MassUploadDaoImpl-->findByErrStatusInMassUploadHistTbl : Start");
		 return jdbcTemplate.query(Queries.FIND_BY_ERR_STATUS_FROM_MASS_UPLOAD, new Object[] { nstrtStatus,inprStatus },new ResultSetExtractor<List<BhEcsMassUploadHistory>>() {
				@Override 
				public List<BhEcsMassUploadHistory> extractData(ResultSet rs) throws SQLException {
			    	  List<BhEcsMassUploadHistory> li= new ArrayList<>();
			    	  BhEcsMassUploadHistory bhEcsMassUploadHist;
			    	  
			    	 while(rs.next()){  
			    		 bhEcsMassUploadHist = new BhEcsMassUploadHistory();
			    		 bhEcsMassUploadHist.setLoadNumber(rs.getInt("load_number"));
					     li.add(bhEcsMassUploadHist);
				        }
			    	    log.info("MassUploadDaoImpl-->findByErrStatusInMassUploadHistTbl : End");
				        return li;
			      }
		    });
	}
}
