package com.code.fill_the_blank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.code.fill_the_blank.dto.RecommendationResult;
import com.code.fill_the_blank.service.RecommendationService;

@RestController
public class RecommendationController {

	private final RecommendationService recommendationService;
	
	public RecommendationController(RecommendationService recommendationService) {
		this.recommendationService = recommendationService;
	}
	
	@GetMapping("/recommendation/{idleId}")
	public List<RecommendationResult> getRecommendations(@PathVariable Long idleId) {
	    return recommendationService.getRecommendations(idleId);
	}
	
	// 추천 결과를 DB에 저장
	@GetMapping("/recommendation/calculate")
	public String calculateRecommendation() {
		recommendationService.calculateRecommendations();
		
		return "추천 점수 계산 및 저장 완료";
	}
	
	
	
}
