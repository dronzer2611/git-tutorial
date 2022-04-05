package com.bh.ecs.integration.dto;

public class CostingReport {
private String employeeId;
private String empName;
private String empStatus;
private String ssoId;
private String locCountry;
private String jobFamily;
private String jobFamilyGroup;
private String businessTitle;
private String organization;
private String businessSegment;
private String subBusiness;
private String wdCompanyId;
private String wdCostCenter;
private String wdRequestType;
private String costingRequestType;
private String goldId;
private String effectiveDate;
private String clConcurGroup;
private String clDirectIndirect;
private String currentCostingString;
private String clCompanyCode;
private String clCostCenter;
private String clFunctionCode;
private String clProductLine;
private String clProjectCode;
private String clReferCode;
private String clCostType;
private String clLegacyAdn;
private String businessRegion;
private String costingRequestStatus;
private String caList;
private String lastUpdatedBy;
private String lastUpdatedDate;
private String rechargeapproved;

public CostingReport() {
	super();
}

public String getBusinessRegion() {
	return businessRegion;
}

public void setBusinessRegion(String businessRegion) {
	this.businessRegion = businessRegion;
}

public String getJobFamily() {
	return jobFamily;
}
public void setJobFamily(String jobFamily) {
	this.jobFamily = jobFamily;
}
public String getJobFamilyGroup() {
	return jobFamilyGroup;
}
public void setJobFamilyGroup(String jobFamilyGroup) {
	this.jobFamilyGroup = jobFamilyGroup;
}
public String getBusinessTitle() {
	return businessTitle;
}
public void setBusinessTitle(String businessTitle) {
	this.businessTitle = businessTitle;
}
public String getOrganization() {
	return organization;
}
public void setOrganization(String organization) {
	this.organization = organization;
}
public String getLastUpdatedBy() {
	return lastUpdatedBy;
}
public void setLastUpdatedBy(String lastUpdatedBy) {
	this.lastUpdatedBy = lastUpdatedBy;
}

public String getRechargeapproved() {
	return rechargeapproved;
}
public void setRechargeapproved(String rechargeapproved) {
	this.rechargeapproved = rechargeapproved;
}
public String getCaList() {
	return caList;
}
public void setCaList(String caList) {
	this.caList = caList;
}
public String getEmpStatus() {
	return empStatus;
}
public void setEmpStatus(String empStatus) {
	this.empStatus = empStatus;
}
public String getCostingRequestStatus() {
	return costingRequestStatus;
}
public void setCostingRequestStatus(String costingRequestStatus) {
	this.costingRequestStatus = costingRequestStatus;
}

public String getEmployeeId() {
	return employeeId;
}
public void setEmployeeId(String employeeId) {
	this.employeeId = employeeId;
}
public String getEmpName() {
	return empName;
}
public void setEmpName(String empName) {
	this.empName = empName;
}
public String getSsoId() {
	return ssoId;
}
public void setSsoId(String ssoId) {
	this.ssoId = ssoId;
}
public String getLocCountry() {
	return locCountry;
}
public void setLocCountry(String locCountry) {
	this.locCountry = locCountry;
}
public String getBusinessSegment() {
	return businessSegment;
}
public void setBusinessSegment(String businessSegment) {
	this.businessSegment = businessSegment;
}
public String getSubBusiness() {
	return subBusiness;
}
public void setSubBusiness(String subBusiness) {
	this.subBusiness = subBusiness;
}
public String getWdCompanyId() {
	return wdCompanyId;
}
public void setWdCompanyId(String wdCompanyId) {
	this.wdCompanyId = wdCompanyId;
}
public String getWdCostCenter() {
	return wdCostCenter;
}
public void setWdCostCenter(String wdCostCenter) {
	this.wdCostCenter = wdCostCenter;
}
public String getWdRequestType() {
	return wdRequestType;
}
public void setWdRequestType(String wdRequestType) {
	this.wdRequestType = wdRequestType;
}
public String getCostingRequestType() {
	return costingRequestType;
}
public void setCostingRequestType(String costingRequestType) {
	this.costingRequestType = costingRequestType;
}
public String getGoldId() {
	return goldId;
}
public void setGoldId(String goldId) {
	this.goldId = goldId;
}
public String getEffectiveDate() {
	return effectiveDate;
}
public void setEffectiveDate(String effectiveDate) {
	this.effectiveDate = effectiveDate;
}
public String getClConcurGroup() {
	return clConcurGroup;
}
public void setClConcurGroup(String clConcurGroup) {
	this.clConcurGroup = clConcurGroup;
}
public String getClDirectIndirect() {
	return clDirectIndirect;
}
public void setClDirectIndirect(String clDirectIndirect) {
	this.clDirectIndirect = clDirectIndirect;
}
public String getCurrentCostingString() {
	return currentCostingString;
}
public void setCurrentCostingString(String currentCostingString) {
	this.currentCostingString = currentCostingString;
}
public String getClCompanyCode() {
	return clCompanyCode;
}
public void setClCompanyCode(String clCompanyCode) {
	this.clCompanyCode = clCompanyCode;
}
public String getClCostCenter() {
	return clCostCenter;
}
public void setClCostCenter(String clCostCenter) {
	this.clCostCenter = clCostCenter;
}
public String getClFunctionCode() {
	return clFunctionCode;
}
public void setClFunctionCode(String clFunctionCode) {
	this.clFunctionCode = clFunctionCode;
}
public String getClProductLine() {
	return clProductLine;
}
public void setClProductLine(String clProductLine) {
	this.clProductLine = clProductLine;
}
public String getClProjectCode() {
	return clProjectCode;
}
public void setClProjectCode(String clProjectCode) {
	this.clProjectCode = clProjectCode;
}
public String getClReferCode() {
	return clReferCode;
}
public void setClReferCode(String clReferCode) {
	this.clReferCode = clReferCode;
}
public String getClCostType() {
	return clCostType;
}
public void setClCostType(String clCostType) {
	this.clCostType = clCostType;
}
public String getClLegacyAdn() {
	return clLegacyAdn;
}
public void setClLegacyAdn(String clLegacyAdn) {
	this.clLegacyAdn = clLegacyAdn;
}

public String getLastUpdatedDate() {
	return lastUpdatedDate;
}
public void setLastUpdatedDate(String lastUpdatedDate) {
	this.lastUpdatedDate = lastUpdatedDate;
}

@Override
public String toString() {
	return "CostingReport [employeeId=" + employeeId + ", empName=" + empName + ", empStatus=" + empStatus + ", ssoId="
			+ ssoId + ", locCountry=" + locCountry + ", jobFamily=" + jobFamily + ", jobFamilyGroup=" + jobFamilyGroup
			+ ", businessTitle=" + businessTitle + ", organization=" + organization + ", businessSegment="
			+ businessSegment + ", subBusiness=" + subBusiness + ", wdCompanyId=" + wdCompanyId + ", wdCostCenter="
			+ wdCostCenter + ", wdRequestType=" + wdRequestType + ", costingRequestType=" + costingRequestType
			+ ", goldId=" + goldId + ", effectiveDate=" + effectiveDate + ", clConcurGroup=" + clConcurGroup
			+ ", clDirectIndirect=" + clDirectIndirect + ", currentCostingString=" + currentCostingString
			+ ", clCompanyCode=" + clCompanyCode + ", clCostCenter=" + clCostCenter + ", clFunctionCode="
			+ clFunctionCode + ", clProductLine=" + clProductLine + ", clProjectCode=" + clProjectCode
			+ ", clReferCode=" + clReferCode + ", clCostType=" + clCostType + ", clLegacyAdn=" + clLegacyAdn
			+ ", businessRegion=" + businessRegion + ", costingRequestStatus=" + costingRequestStatus + ", caList="
			+ caList + ", lastUpdatedBy=" + lastUpdatedBy + ", lastUpdatedDate=" + lastUpdatedDate
			+ ", rechargeapproved=" + rechargeapproved + "]";
}


}
