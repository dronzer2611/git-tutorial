package com.bh.ecs.integration.dto;


public class WorkdayData {
private String empId;
private String costCenterId;
private String companyCode;
private String legacyCostCenter;
private String productLine;
private String projectCode;
private String referenceCode;
private String costType;
private String directIndirect;
private String ledgerAdn;
private String effectiveDate;

public WorkdayData() {
	super();
}

public String getEmpId() {
	return empId;
}
public void setEmpId(String empId) {
	this.empId = empId;
}
public String getCostCenterId() {
	return costCenterId;
}
public void setCostCenterId(String costCenterId) {
	this.costCenterId = costCenterId;
}
public String getCompanyCode() {
	return companyCode;
}
public void setCompanyCode(String companyCode) {
	this.companyCode = companyCode;
}
public String getLegacyCostCenter() {
	return legacyCostCenter;
}
public void setLegacyCostCenter(String legacyCostCenter) {
	this.legacyCostCenter = legacyCostCenter;
}
public String getProductLine() {
	return productLine;
}
public void setProductLine(String productLine) {
	this.productLine = productLine;
}
public String getProjectCode() {
	return projectCode;
}
public void setProjectCode(String projectCode) {
	this.projectCode = projectCode;
}
public String getReferenceCode() {
	return referenceCode;
}
public void setReferenceCode(String referenceCode) {
	this.referenceCode = referenceCode;
}
public String getCostType() {
	return costType;
}
public void setCostType(String costType) {
	this.costType = costType;
}
public String getDirectIndirect() {
	return directIndirect;
}
public void setDirectIndirect(String directIndirect) {
	this.directIndirect = directIndirect;
}
public String getLedgerAdn() {
	return ledgerAdn;
}
public void setLedgerAdn(String ledgerAdn) {
	this.ledgerAdn = ledgerAdn;
}
public String getEffectiveDate() {
	return effectiveDate;
}
public void setEffectiveDate(String effectiveDate) {
	this.effectiveDate = effectiveDate;
}
@Override
public String toString() {
	return "WorkdayData [empId=" + empId + ", costCenterId=" + costCenterId + ", companyCode=" + companyCode
			+ ", legacyCostCenter=" + legacyCostCenter + ", productLine=" + productLine + ", projectCode=" + projectCode
			+ ", referenceCode=" + referenceCode + ", costType=" + costType + ", directIndirect=" + directIndirect
			+ ", ledgerAdn=" + ledgerAdn + ", effectiveDate=" + effectiveDate + "]";
}
}
