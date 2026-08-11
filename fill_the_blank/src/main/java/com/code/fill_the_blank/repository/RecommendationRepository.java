package com.code.fill_the_blank.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.code.fill_the_blank.dto.RecommendationInput;
import com.code.fill_the_blank.dto.RecommendationResult;

@Repository
public class RecommendationRepository {

	private final JdbcTemplate jdbcTemplate;
	
	public RecommendationRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	// 430개 행정동의 전체 데이터를 조회
	public List<RecommendationInput> findAllAreaData() {
		
		String sql = """
				SELECT
					m.area_code,
                m.culture_facility_cnt,
                m.welfare_facility_cnt,
                m.commercial_area_cnt,
                m.total_pop,
                m.youth_pop,
                m.elder_pop,
                m.avg_life_pop,
                m.avg_youth_life_pop,
                m.avg_elder_life_pop
				FROM idle_space_analysis m
				""";
		
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			RecommendationInput data = new RecommendationInput();
			
			data.setAreaCode(rs.getString("area_code"));
			data.setCultureFacilityCnt(rs.getInt("culture_facility_cnt"));
            data.setWelfareFacilityCnt(rs.getInt("welfare_facility_cnt"));
            data.setCommercialAreaCnt(rs.getInt("commercial_area_cnt"));
            data.setTotalPop(rs.getInt("total_pop"));
            data.setYouthPop(rs.getInt("youth_pop"));
            data.setElderPop(rs.getInt("elder_pop"));
            data.setAvgLifePop(rs.getDouble("avg_life_pop"));
            data.setAvgYouthLifePop(rs.getDouble("avg_youth_life_pop"));
            data.setAvgElderLifePop(rs.getDouble("avg_elder_life_pop"));

            return data;
		});
		
		
	}
	
	
	// 49개 유휴공간 + 해당 행정동 데이터를 조회
	public List<RecommendationInput> findIdleSpaces() {
		String sql = """
				SELECT
					i.idle_id,
				    i.area_code,
                    i.facility_size,

                    m.culture_facility_cnt,
                    m.welfare_facility_cnt,
                    m.commercial_area_cnt,

                    m.total_pop,
                    m.youth_pop,
                    m.elder_pop,

                    m.avg_life_pop,
                    m.avg_youth_life_pop,
                    m.avg_elder_life_pop
				    
				    FROM idle_space i
				    JOIN idle_space_analysis m
    				    ON i.area_code = m.area_code
    				    
    				ORDER BY i.idle_id
				""";
		
		return jdbcTemplate.query(sql, (rs, rowNum) ->{
			RecommendationInput data = new RecommendationInput();
			
            data.setIdleId(rs.getLong("idle_id"));
            data.setAreaCode(rs.getString("area_code"));

            data.setFacilitySize(rs.getDouble("facility_size"));

            data.setCultureFacilityCnt(rs.getInt("culture_facility_cnt"));

            data.setWelfareFacilityCnt(rs.getInt("welfare_facility_cnt"));

            data.setCommercialAreaCnt(rs.getInt("commercial_area_cnt"));

            data.setTotalPop(rs.getInt("total_pop"));

            data.setYouthPop(rs.getInt("youth_pop"));

            data.setElderPop(rs.getInt("elder_pop"));

            data.setAvgLifePop(rs.getDouble("avg_life_pop"));

            data.setAvgYouthLifePop(rs.getDouble("avg_youth_life_pop"));

            data.setAvgElderLifePop(rs.getDouble("avg_elder_life_pop"));

            return data;
			
		});
		
	}
	
	
	
    // 추천 결과 저장
	public void insertRecommendation(
			Long idleId,
			String recommendationType,
			Double score,
			Integer rank,
			String recommendationReason
			) {
		
		
	    String sql = """
	            INSERT INTO recommendation_score
	            (
	                idle_id,
	                recommendation_type,
	                score,
	                recommendation_rank,
	                recommendation_reason
	            )
	            VALUES (?, ?, ?, ?, ?)
	            """;

	    jdbcTemplate.update(sql, idleId, recommendationType, score, rank, recommendationReason);
		
	}
	
	
	// 기존 추천 결과 삭제
	public void deleteAllRecommendations() {
		String sql = """
				DELETE FROM recommendation_score
				""";
		jdbcTemplate.update(sql);
	}
	
	
	// 추천 결과 조회하기
	public List<RecommendationResult> findRecommendationsByIdleId(Long idleId) {

	    String sql = """
	        SELECT
	            idle_id,
	            recommendation_type,
	            score,
	            recommendation_rank,
	            recommendation_reason
	        FROM recommendation_score
	        WHERE idle_id = ?
	        ORDER BY recommendation_rank
	        """;

	    return jdbcTemplate.query(sql, (rs, rowNum) ->
	        new RecommendationResult(
	            rs.getLong("idle_id"),
	            rs.getString("recommendation_type"),
	            rs.getDouble("score"),
	            rs.getInt("recommendation_rank"),
	            rs.getString("recommendation_reason")
	        ),
	        idleId
	    );
	}
	
	
	
	
	
	
	
}
