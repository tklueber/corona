package com.hofbieber.grabdatacoronavirus.data.repository;

import com.hofbieber.grabdatacoronavirus.data.entity.VirusCaseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirusCaseRepository extends CrudRepository<VirusCaseEntity, Double> {
}
