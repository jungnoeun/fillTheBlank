package com.code.fill_the_blank.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
	private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; // refresh token 유효기간 7일
	
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
	
	public String createRefreshToken(Member member) {
		Date now = new Date();
		Date expiry = new Date(now.getTime()+refreshTokenValidity);
		
		return Jwts.builder()
				.subject(member.getUsername())
				.issuedAt(now)
				.expiration(expiry)
				.signWith(key)
				.compact();
	}
	
	public SecretKey getKey() {
		return key;
	}
	
	// refresh token의 서명과 유효기간 확인 -> 토큰에 저장된 사용자 이름(subject) 가져옴
	public String getUsernameFromToken(String token) { 
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	// JWT에서 만료시간을 가져오는 메서드
	public LocalDateTime getExpireationFromToken(String token) {
		
		Date expiration = Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration();
		
		return expiration.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
	
	// accesss token에서 회원 Id 추출 - 서명과 유효기간 확인 후 추출, 블랙리스트 여부 확인X -> 인증 필터 통과 후 실행 필수
	public Long getMemeberIdFromToken(String token) {
		Number memberId = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("memberId", Number.class);
		
		if(memberId == null) {
			throw new IllegalArgumentException("JWT에 회원 ID가 없습니다.");
		}
		
		return memberId.longValue();
	}
	
	// 만료 시각 조회
	public Instant getExpirationInsatnt(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.toInstant();
	}
}
