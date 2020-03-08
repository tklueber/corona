package com.hofbieber.grabdatacoronavirus.business.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ReportTuple {
    private Date date;
    private String country;
    private String countryId;
    private double confCases;
    private double death;
    private boolean isEu;
}
