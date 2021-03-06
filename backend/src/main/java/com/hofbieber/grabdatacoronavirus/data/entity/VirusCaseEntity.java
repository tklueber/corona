package com.hofbieber.grabdatacoronavirus.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "viruscases", schema = "public", catalog = "coronadata")
@Data
public class VirusCaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;
    @Basic
    @Column(name = "date", nullable = false)
    private Date date;
    @Basic
    @Column(name = "cases", nullable = true)
    private double cases;
    @Basic
    @Column(name = "deaths", nullable = true)
    private double deaths;
    @ManyToOne
    @JoinColumn(name = "country", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private RegionEntity country;
}
