package com.bh.ecs.integration.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bh.ecs.integration.common.utility.Constants;
import com.bh.ecs.integration.dao.MassUploadDao;
import com.bh.ecs.integration.entity.BhEcsMassUploadHistory;

@Service
public class MassUploadServiceImpl implements MassUploadService{

@Autowired
MassUploadDao massUploadDao;


@PersistenceContext
private EntityManager em;

private Logger log = LoggerFactory.getLogger(MassUploadServiceImpl.class);

@Override
public void validateRulesService() {

	List<BhEcsMassUploadHistory> massUploadHistoryList = massUploadDao.findByErrStatusInMassUploadHistTbl(Constants.NOT_STARTED,Constants.IN_PROGRESS);

        for (BhEcsMassUploadHistory massUploadHistory : massUploadHistoryList) {
            
            String str = massUploadDao.validateRecords(massUploadHistory.getLoadNumber());
            log.info("Validation rule check service completed for load id ->{} and status -> {}",
                    massUploadHistory.getLoadNumber(), str);
        }
    }

@Override
public void approvedQueue() {
	List<BhEcsMassUploadHistory> massUploadHistoryList = massUploadDao.findByErrorStatus(Constants.IN_PROGRESS);
	for(BhEcsMassUploadHistory massupload : massUploadHistoryList) {
		
	  String str = massUploadDao.approvedQueue(massupload.getLoadNumber());
	  log.info("Approved Queue Scheduler completed for load id ->{} and status -> {}",
			  massupload.getLoadNumber(), str);
	}
	
}


}
