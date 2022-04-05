package com.bh.ecs.integration.dao;


import java.util.List;
import com.bh.ecs.integration.entity.BhEcsMassUploadHistory;

public interface MassUploadDao {
	
 String validateRecords(Integer loadNumber);
 
 String approvedQueue(Integer loadNo);
 
 List<BhEcsMassUploadHistory> findByErrorStatus(String status);
 
 List<BhEcsMassUploadHistory> findByErrStatusInMassUploadHistTbl(String nstrtStatus, String inprStatus);
 
}
