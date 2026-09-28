package com.code.fill_the_blank.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.code.fill_the_blank.dto.Member;

@Repository
public class MemberRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public MemberRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public int save(Member member) {
		String sql = """
				INSERT INTO member (
					username,
					password,
					name,
					email
				)
				VALUES (?,?,?,?)
				""";
		
		return jdbcTemplate.update(
				sql, 
				member.getUsername(), 
				member.getPassword(),
				member.getName(),
				member.getEmail()
				);	
				
	}
}
