package com.code.fill_the_blank.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.code.fill_the_blank.dto.IdleSpace;

@Repository
public class IdleSpaceRepository {

	private final JdbcTemplate jdbcTemplate;
	
	public IdleSpaceRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<IdleSpace> findAll() {
		
		String sql = """
				SELECT 
					 idle_id,
                     area_code,
                     facility_name,
                     address,
                     facility_size,
                     latitude,
                     longtitude,
                     area_name
                FROM idle_space
                ORDER BY idle_id
				
				""";
		
		return jdbcTemplate.query(sql, (rs, rowNum) ->{
			
			IdleSpace idleSpace = new IdleSpace();
			
			idleSpace.setIdleId(rs.getLong("idle_id"));
            idleSpace.setAreaCode(rs.getString("area_code"));
            idleSpace.setFacilityName(rs.getString("facility_name"));
            idleSpace.setAddress(rs.getString("address"));
            idleSpace.setFacilitySize(rs.getDouble("facility_size"));
            idleSpace.setLatitude(rs.getDouble("latitude"));
            idleSpace.setLongtitude(rs.getDouble("longtitude"));
            idleSpace.setAreaName(rs.getString("area_name"));
        
            
            return idleSpace;
			
		});
		
	}
	
}
