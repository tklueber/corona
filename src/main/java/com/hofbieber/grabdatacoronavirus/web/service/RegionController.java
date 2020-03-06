package com.hofbieber.grabdatacoronavirus.web.service;

import com.hofbieber.grabdatacoronavirus.data.entity.RegionEntity;
import com.hofbieber.grabdatacoronavirus.data.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/region")
public class RegionController {

    @Autowired
    RegionRepository regionRepository;

    @RequestMapping(method = RequestMethod.GET)
    List<RegionEntity> findAll() {
        List<RegionEntity> regions = new ArrayList<>();

        regionRepository.findAll().forEach(regionEntity -> regions.add(regionEntity));

        return regions;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    RegionEntity findById(@PathVariable(name = "id") String id) {
        return regionRepository.findById(id).get();
    }

}
