package com.code.fill_the_blank.service;

import org.springframework.stereotype.Service;

import com.code.fill_the_blank.dto.Member;
import com.code.fill_the_blank.repository.MemberRepository;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	public void signup(Member member) {
		memberRepository.save(member);
	}
	
}
