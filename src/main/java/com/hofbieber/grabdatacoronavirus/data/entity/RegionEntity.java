package com.hofbieber.grabdatacoronavirus.data.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hofbieber.grabdatacoronavirus.business.domain.ReportTuple;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;
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
    @JsonManagedReference
    private boolean eu;

    @Transient
    @JsonGetter(value = "sumCases")
    public int sumCases() {
        return virusCaseList.stream()
                .mapToInt(x -> Double.valueOf(x.getCases()).intValue())
                .sum();
    }

    @Transient
    @JsonGetter(value = "sumDeaths")
    public int sumDeaths() {
        return virusCaseList.stream()
                .mapToInt(x -> Double.valueOf(x.getDeaths()).intValue())
                .sum();
    }

    @Transient
    @JsonGetter(value = "firstCase")
    public Date firstCase() {
        return virusCaseList.stream()
                .sorted(Comparator.comparing(VirusCaseEntity::getDate))
                .filter(virusCaseEntity -> virusCaseEntity.getCases() > 0)
                .map(virusCaseEntity -> virusCaseEntity.getDate())
                .findFirst().get();
    }

    @Transient
    @JsonGetter(value = "lastCase")
    public Date lastCase() {
        return virusCaseList.stream()
                .sorted(Comparator.comparing(VirusCaseEntity::getDate).reversed())
                .filter(virusCaseEntity -> virusCaseEntity.getCases() > 0)
                .map(virusCaseEntity -> virusCaseEntity.getDate())
                .findFirst().get();
    }

    @Transient
    @JsonGetter(value = "mortality")
    public Double mortality() {
        return ((double) this.sumDeaths() / (double) this.sumCases());
    }

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
