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
	
	public Member findByUsername(String username) {
		String sql = """
				SELECT
					member_id,
					username,
					password,
					name,
					email
				FROM member
				WHERE username = ?
				""";
		
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->{
				
			Member member = new Member();
			member.setMemberId(rs.getLong("member_id"));
			member.setUsername(rs.getString("username"));
			member.setPassword(rs.getString("password"));
			member.setName(rs.getString("name"));
			member.setEmail(rs.getString("email"));
			
			return member;
			}, username
		);
	}
}
