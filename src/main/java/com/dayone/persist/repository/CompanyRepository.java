package com.dayone.persist.repository;

import com.dayone.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    Optional<CompanyEntity> findByName(String companyName); // Optional << NullPointException 핸들링 가능
//    Optional<CompanyEntity> findByTicker(String ticker);”

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);

    Optional<CompanyEntity> findByTicker(String ticker);
}