package com.code.fill_the_blank.repository;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccessTokenBlacklistRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public AccessTokenBlacklistRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	// 폐기할 Access Token 저장
	public void save(String token, LocalDateTime expiresAt) {
		
		String sql = """
				INSERT INTO access_token_blacklist
				(token_value, expires_at)
				VALUES (?,?)
				""";
		
		jdbcTemplate.update(sql, token, Timestamp.valueOf(expiresAt));
		
	}
	
	// 블랙리스트에 등록된 토큰인지 확인
	public boolean existsByToken(String token) {
		String sql = """
				SELECT COUNT(*)
				FROM access_token_blacklist
				WHERE token_value = ?
				""";
		
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, token);
		
		return count != null && count > 0;
	}
	
}
