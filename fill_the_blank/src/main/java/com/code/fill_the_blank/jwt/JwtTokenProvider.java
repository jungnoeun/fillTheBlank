package com.code.fill_the_blank.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.code.fill_the_blank.dto.Member;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtTokenProvider {
	private final SecretKey key;
	
	private final long accessTokenValidity = 1000L * 60 * 30; // access token 유효기간 30분
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}
	public String createAccessToken(Member member) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + accessTokenValidity); // access token 만료 처리
		
		return Jwts.builder()
				.subject(member.getUsername())
				.claim("memberId", member.getMemberId())
				.claim("name", member.getUsername())
				.issuedAt(now)
				.expiration(expiry)
				.signWith(key)
				.compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
			.verifyWith(key) // 서명 검증
			.build()
			.parseSignedClaims(token); // Signed Claims JWT 파싱
			
			return true;
		} catch(ExpiredJwtException e) {
			System.out.println("JWT가 만료되었습니다.");
			return false;
			
		} catch (JwtException | IllegalArgumentException e) {
			System.out.println("유효하지 않은 JWT입니다.");
			return false;
		}
	}
	
}
