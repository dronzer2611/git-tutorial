package com.bh.ecs.integration.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
 
@Component
@PropertySource("classpath:workday.properties")
public class WorkdayConfiguration {

	@Value("${workday.publicKey}")
    private String pgpPublicKeyFileName;

    public String getPgpPublicKeyFileName() {
        return pgpPublicKeyFileName;
    }
 
    @Value("${workday.privateKey}")
    private String pgpPrivateKeyFileName;
    
    public String getPgpPrivateKeyFileName() {
        return pgpPrivateKeyFileName;
    }

 

}