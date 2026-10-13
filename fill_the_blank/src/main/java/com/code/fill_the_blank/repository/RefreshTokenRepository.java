package com.code.fill_the_blank.repository;

import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public RefreshTokenRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public int save(Long memberId, String token, LocalDateTime expireAt) {
		String sql = """
				INSERT INTO refresh_token(
					member_id,
					token_value,
					expires_at
				)
				VALUES(?,?,?)
				""";
		
		return jdbcTemplate.update(sql, memberId, token, expireAt);
	}
	
	public boolean existsByToken(String token) {
		String sql = """
				SELECT COUNT(*)
				FROM refresh_token
				WHERE token_value=?
				""";
		
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, token);
		
		return count!=null && count > 0;
	}
	
	public void deleteByToken(String token) {
		String sql = """
				DELETE FROM refresh_token
				WHERE token_value=?
				""";
		
		jdbcTemplate.update(sql, token);
	}
}
