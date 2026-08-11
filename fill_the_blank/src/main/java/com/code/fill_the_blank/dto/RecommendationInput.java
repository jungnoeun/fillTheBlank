package com.code.fill_the_blank.dto;

// DB에서 가져온 데이터를 담을 객체
public class RecommendationInput {
	private Long idleId;
	private String areaCode;
	private Double facilitySize;
	
	private Integer cultureFacilityCnt;
	private Integer welfareFacilityCnt;
	private Integer commercialAreaCnt;
	
	private Integer totalPop;
	private Integer youthPop ;
	private Integer elderPop;
	
	private Double avgLifePop;
	private Double avgYouthLifePop;
	private Double avgElderLifePop;
	
	public Long getIdleId() {
		return idleId;
	}
	public void setIdleId(Long idleId) {
		this.idleId = idleId;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public Double getFacilitySize() {
		return facilitySize;
	}
	public void setFacilitySize(Double facilitySize) {
		this.facilitySize = facilitySize;
	}
	public Integer getCultureFacilityCnt() {
		return cultureFacilityCnt;
	}
	public void setCultureFacilityCnt(Integer cultureFacilityCnt) {
		this.cultureFacilityCnt = cultureFacilityCnt;
	}
	public Integer getWelfareFacilityCnt() {
		return welfareFacilityCnt;
	}
	public void setWelfareFacilityCnt(Integer welfareFacilityCnt) {
		this.welfareFacilityCnt = welfareFacilityCnt;
	}
	public Integer getCommercialAreaCnt() {
		return commercialAreaCnt;
	}
	public void setCommercialAreaCnt(Integer commercialAreaCnt) {
		this.commercialAreaCnt = commercialAreaCnt;
	}
	public Integer getTotalPop() {
		return totalPop;
	}
	public void setTotalPop(Integer totalPop) {
		this.totalPop = totalPop;
	}
	public Integer getYouthPop() {
		return youthPop;
	}
	public void setYouthPop(Integer youthPop) {
		this.youthPop = youthPop;
	}
	public Integer getElderPop() {
		return elderPop;
	}
	public void setElderPop(Integer elderPop) {
		this.elderPop = elderPop;
	}
	public Double getAvgLifePop() {
		return avgLifePop;
	}
	public void setAvgLifePop(Double avgLifePop) {
		this.avgLifePop = avgLifePop;
	}
	public Double getAvgYouthLifePop() {
		return avgYouthLifePop;
	}
	public void setAvgYouthLifePop(Double avgYouthLifePop) {
		this.avgYouthLifePop = avgYouthLifePop;
	}
	public Double getAvgElderLifePop() {
		return avgElderLifePop;
	}
	public void setAvgElderLifePop(Double avgElderLifePop) {
		this.avgElderLifePop = avgElderLifePop;
	}
	
	
	
	
	
}
