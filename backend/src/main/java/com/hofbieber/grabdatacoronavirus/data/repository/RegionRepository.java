package com.hofbieber.grabdatacoronavirus.data.repository;

import com.hofbieber.grabdatacoronavirus.data.entity.RegionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends CrudRepository<RegionEntity, String> {
}
