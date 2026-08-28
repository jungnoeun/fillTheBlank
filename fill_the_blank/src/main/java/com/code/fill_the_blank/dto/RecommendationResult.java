package com.code.fill_the_blank.dto;

// 추천결과를 담을 DTO
public class RecommendationResult {

	private Long idleId;
	private String recommendationType;
	private Double score;
	private Integer rank;
	private String recommendationReason;
	
	public RecommendationResult(
			Long idleId,
			String recommendationType,
			Double score,
			Integer rank,
			String recommendationReason
			) {
		
		this.idleId = idleId;
		this.recommendationType = recommendationType;
		this.score = score;
		this.rank = rank;
		this.recommendationReason = recommendationReason;
		
	}

	public Long getIdleId() {
		return idleId;
	}

	public String getRecommendationType() {
		return recommendationType;
	}

	public Double getScore() {
		return score;
	}

	public Integer getRank() {
		return rank;
	}

	public String getRecommendationReason() {
		return recommendationReason;
	}
	
	
	
}
