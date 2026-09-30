package com.code.fill_the_blank.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.code.fill_the_blank.dto.Member;
import com.code.fill_the_blank.repository.MemberRepository;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder; 
	
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	public void signup(Member member) {
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		memberRepository.save(member);
	}
	
	public Member login(String username, String password) {
		Member member;
		
		try {
			member = memberRepository.findByUsername(username);
		} catch (EmptyResultDataAccessException e) {
			// TODO: handle exception
			return null;
		}
		
		boolean passwordMatch = passwordEncoder.matches(password, member.getPassword());
		
		if(!passwordMatch) {
			return null;
		}
		
		return member;
	}
	
}
