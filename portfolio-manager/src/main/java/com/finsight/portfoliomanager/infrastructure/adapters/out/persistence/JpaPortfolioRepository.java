package com.finsight.portfoliomanager.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finsight.portfoliomanager.infrastructure.adapters.out.persistence.Entities.PortfolioEntity;

public interface JpaPortfolioRepository extends JpaRepository<PortfolioEntity, UUID> {

    List<PortfolioEntity> findByUserId(UUID userId);

}
