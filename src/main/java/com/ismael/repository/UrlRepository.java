package com.ismael.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ismael.entity.UrlEntity;

public interface UrlRepository extends JpaRepository<UrlEntity, Long>{

	Optional<UrlEntity> findByShortHash(String shortHas);
	
}
