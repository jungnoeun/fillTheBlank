package com.code.fill_the_blank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.fill_the_blank.dto.IdleSpace;
import com.code.fill_the_blank.repository.IdleSpaceRepository;

@Service
public class IdleSpaceService {

	private final IdleSpaceRepository repository;
	
	public IdleSpaceService(IdleSpaceRepository repository) {
		this.repository = repository;
	}
	
	
	public List<IdleSpace> findAll() {
		return repository.findAll();
	}
	
}
