package com.code.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBConnectionTest implements CommandLineRunner {

	private final JdbcTemplate jdbcTemplate;
	
	public DBConnectionTest(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public void run(String... args) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM idle_space",
				Integer.class
		);
	
	
		System.out.println("===============================");
		System.out.println("Oracle DB 연결 성공!!");
		System.out.println("idle_space 데이터 개수: " + count);
		System.out.println("===============================");
		
	}
	
	
}
