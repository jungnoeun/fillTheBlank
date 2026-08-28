package com.code.fill_the_blank.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.fill_the_blank.dto.RecommendationInput;
import com.code.fill_the_blank.dto.RecommendationResult;
import com.code.fill_the_blank.repository.RecommendationRepository;

@Service
public class RecommendationService {

	private final RecommendationRepository repository;
	
	private record ReasonScore(
	        double score,
	        String reason) {
	}
	
	public RecommendationService(RecommendationRepository repository) {
		this.repository = repository;
	}
	
	// 추천 결과 조회
	public List<RecommendationResult> getRecommendations(Long idleId) {
	    return repository.findRecommendationsByIdleId(idleId);
	}
	
	
	// DB에 추천 결과(점수, 순위, 이유) 저장하기
	@Transactional
	public void calculateRecommendations() {
		
		// 1. 430개 행정동 데이터 조회
		List<RecommendationInput> areaData = repository.findAllAreaData();
		
		// 2. 49개 유휴공간 + 해당 행정동 데이터 조회
		List<RecommendationInput> idleSpaces = repository.findIdleSpaces();
		
		if (areaData.isEmpty()) {
            throw new IllegalStateException("mapreduce_result에 데이터가 없습니다.");
        }

        if (idleSpaces.isEmpty()) {
            throw new IllegalStateException("idle_space에 데이터가 없습니다.");
        }
		
        // 3. 430개 행정동을 기준으로 최소/최대값 계산
        double minCulture = areaData.stream()
        				.mapToDouble(x->x.getCultureFacilityCnt())
        				.min()
        				.orElse(0);
        
        double maxCulture = areaData.stream()
                        .mapToDouble(x -> x.getCultureFacilityCnt())
                        .max()
                        .orElse(0);


        double minWelfare = areaData.stream()
                        .mapToDouble(x -> x.getWelfareFacilityCnt())
                        .min()
                        .orElse(0);

        double maxWelfare = areaData.stream()
                        .mapToDouble(x -> x.getWelfareFacilityCnt())
                        .max()
                        .orElse(0);


        double minCommercial = areaData.stream()
                        .mapToDouble(x -> x.getCommercialAreaCnt())
                        .min()
                        .orElse(0);

        double maxCommercial = areaData.stream()
                        .mapToDouble(x -> x.getCommercialAreaCnt())
                        .max()
                        .orElse(0);


        double minYouth = areaData.stream()
                        .mapToDouble(x -> x.getYouthPop())
                        .min()
                        .orElse(0);

        double maxYouth = areaData.stream()
                        .mapToDouble(x -> x.getYouthPop())
                        .max()
                        .orElse(0);


        double minElder = areaData.stream()
                        .mapToDouble(x -> x.getElderPop())
                        .min()
                        .orElse(0);

        double maxElder = areaData.stream()
                        .mapToDouble(x -> x.getElderPop())
                        .max()
                        .orElse(0);


        double minTotalPop = areaData.stream()
                        .mapToDouble(x -> x.getTotalPop())
                        .min()
                        .orElse(0);

        double maxTotalPop = areaData.stream()
                        .mapToDouble(x -> x.getTotalPop())
                        .max()
                        .orElse(0);


        double minYouthLife = areaData.stream()
                        .mapToDouble(x -> x.getAvgYouthLifePop())
                        .min()
                        .orElse(0);

        double maxYouthLife = areaData.stream()
                        .mapToDouble(x -> x.getAvgYouthLifePop())
                        .max()
                        .orElse(0);


        double minElderLife = areaData.stream()
                        .mapToDouble(x -> x.getAvgElderLifePop())
                        .min()
                        .orElse(0);

        double maxElderLife = areaData.stream()
                        .mapToDouble(x -> x.getAvgElderLifePop())
                        .max()
                        .orElse(0);


        double minLife = areaData.stream()
                        .mapToDouble(x -> x.getAvgLifePop())
                        .min()
                        .orElse(0);

        double maxLife = areaData.stream()
                        .mapToDouble(x -> x.getAvgLifePop())
                        .max()
                        .orElse(0);


        double minSize = idleSpaces.stream()
                        .mapToDouble(x -> x.getFacilitySize())
                        .min()
                        .orElse(0);

        double maxSize = idleSpaces.stream()
                        .mapToDouble(x -> x.getFacilitySize())
                        .max()
                        .orElse(0);

        // 기존 추천 결과 삭제
        repository.deleteAllRecommendations();

        
        // 4. 49개 유휴공간에 대해 추천 계산
        for (RecommendationInput idle : idleSpaces) {

        	// 문화시설 부족도 점수
            double cultureShortage = shortageScore(idle.getCultureFacilityCnt(), minCulture, maxCulture);
            // 복지시설 부족도 점수
            double welfareShortage = shortageScore(idle.getWelfareFacilityCnt(), minWelfare, maxWelfare);
            // 상권 수 부족도 점수
            double commercialShortage = shortageScore(idle.getCommercialAreaCnt(),minCommercial,maxCommercial);
            // 정년 비율(2~30대)
            double youthScore = normalScore(idle.getYouthPop(), minYouth, maxYouth);
            // 노인 비율(60대 이상)
            double elderScore = normalScore(idle.getElderPop(), minElder, maxElder);
            // 전체 인구 점수
            double totalPopScore = normalScore(idle.getTotalPop(), minTotalPop, maxTotalPop);
            // 청년 생활 인구 점수 
            double youthLifeScore = normalScore(idle.getAvgYouthLifePop(), minYouthLife, maxYouthLife);
            // 노인 생활 인구 점수
            double elderLifeScore = normalScore(idle.getAvgElderLifePop(), minElderLife, maxElderLife);
            // 생활 인구 점수
            double lifeScore = normalScore(idle.getAvgLifePop(), minLife, maxLife);
            // 면적 점수
            double sizeScore = normalScore(idle.getFacilitySize(), minSize, maxSize);


            // 5. 추천 유형별 점수 계산

            double cultureScore =
                    cultureShortage * 0.35
                    + youthScore * 0.20
                    + youthLifeScore * 0.35
                    + sizeScore * 0.10;


            double welfareScore =
                    welfareShortage * 0.35
                    + elderScore * 0.20
                    + elderLifeScore * 0.35
                    + sizeScore * 0.10;


            double commercialScore =
                    commercialShortage * 0.35
                    + totalPopScore * 0.20
                    + lifeScore * 0.35
                    + sizeScore * 0.10;
            
            // 5-2. 추천 이유를 만듬
            String cultureReason = createRecommendationReason(
                            "문화공간",
                            cultureShortage,
                            youthScore,
                            youthLifeScore,
                            sizeScore);
            
            String welfareReason = createRecommendationReason(
                            "복지공간",
                            welfareShortage,
                            elderScore,
                            elderLifeScore,
                            sizeScore);
            
            String commercialReason = createRecommendationReason(
                            "상업공간",
                            commercialShortage,
                            totalPopScore,
                            lifeScore,
                            sizeScore);


            // 6. 세 점수를 리스트에 넣음
            List<RecommendationResult> results = new ArrayList<>();

            results.add(
                    new RecommendationResult(
                            idle.getIdleId(),
                            "문화공간",
                            cultureScore,
                            0,
                            cultureReason));

            results.add(
                    new RecommendationResult(
                            idle.getIdleId(),
                            "복지공간",
                            welfareScore,
                            0,
                            welfareReason));

            results.add(
                    new RecommendationResult(
                            idle.getIdleId(),
                            "상업공간",
                            commercialScore,
                            0,
                            commercialReason));


            // 7. 점수가 높은 순으로 정렬
            results.sort(
                    Comparator.comparing(
                            RecommendationResult::getScore)
                            .reversed());


            // 8. rank 지정 + DB 저장
            for (int i = 0; i < results.size(); i++) {

                RecommendationResult result = results.get(i);

                int rank = i + 1;

                repository.insertRecommendation(
                        result.getIdleId(),
                        result.getRecommendationType(),
                        round(result.getScore()),
                        rank,
                        result.getRecommendationReason()
                );
            }
        }
    }


	// 일반적인 0~100 정규화
    private double normalScore(
            double value,
            double min,
            double max) {

        if (max == min) {
            return 50.0;
        }

        return (value - min) / (max - min) * 100.0;
    }
        
    // 값이 작을수록 높은 점수
    private double shortageScore(
            double value,
            double min,
            double max) {

        if (max == min) {
            return 50.0;
        }

        return (max - value) / (max - min) * 100.0;
    }


    // 소수점 둘째 자리까지
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    private String createRecommendationReason(
            String recommendationType,
            double shortageScore,
            double populationScore,
            double lifePopulationScore,
            double sizeScore) {

        List<ReasonScore> reasons = new ArrayList<>();

        if (recommendationType.equals("문화공간")) {

            reasons.add(new ReasonScore(shortageScore, "문화시설이 부족함"));

            reasons.add(new ReasonScore(populationScore, "청년 인구가 많음"));

            reasons.add(new ReasonScore(lifePopulationScore, "청년 생활인구가 많음"));

            reasons.add(new ReasonScore(sizeScore, "유휴공간 면적이 충분함"));

        } else if (recommendationType.equals("복지공간")) {

            reasons.add(new ReasonScore(shortageScore, "복지시설이 부족함"));

            reasons.add(new ReasonScore(populationScore, "노인 인구가 많음"));

            reasons.add(new ReasonScore(lifePopulationScore, "노인 생활인구가 많음"));

            reasons.add(new ReasonScore(sizeScore, "유휴공간 면적이 충분함"));

        } else if (recommendationType.equals("상업공간")) {

            reasons.add(new ReasonScore(shortageScore, "상업지역이 부족함"));

            reasons.add(new ReasonScore(populationScore, "전체 인구가 많음"));

            reasons.add(new ReasonScore(lifePopulationScore, "생활인구가 많음"));

            reasons.add(new ReasonScore(sizeScore, "유휴공간 면적이 충분함"));
        }

        // 점수가 높은 이유부터 정렬
        reasons.sort(
                Comparator.comparing(
                        ReasonScore::score)
                        .reversed()
        );

        // 상위 3개만 선택
        return reasons.stream()
                .limit(3)
                .map(ReasonScore::reason)
                .collect(java.util.stream.Collectors.joining(", "));
    }
    
    
	
}
