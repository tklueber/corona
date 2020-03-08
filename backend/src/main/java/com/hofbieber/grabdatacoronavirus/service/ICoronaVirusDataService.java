package com.hofbieber.grabdatacoronavirus.service;

import com.hofbieber.grabdatacoronavirus.business.domain.ReportTuple;

import java.util.List;

public interface ICoronaVirusDataService {
    public List<ReportTuple> fetchVirusData();
}
