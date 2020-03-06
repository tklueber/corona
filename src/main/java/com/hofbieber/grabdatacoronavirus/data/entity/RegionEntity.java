package com.hofbieber.grabdatacoronavirus.data.entity;

import com.hofbieber.grabdatacoronavirus.business.domain.ReportTuple;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "region", schema = "public", catalog = "coronadata")
@Data
public class RegionEntity {
    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "eu", nullable = false)
    private boolean eu;

    @OneToMany(targetEntity = VirusCaseEntity.class,
            mappedBy = "country",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<VirusCaseEntity> virusCaseList;

    public static RegionEntity fromReportTuple(ReportTuple reportTuple) {
        RegionEntity region = new RegionEntity();
        region.setName(reportTuple.getCountry());
        region.setId(reportTuple.getCountryId());
        region.setEu(reportTuple.isEu());
        return region;
    }
}
