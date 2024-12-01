package com.uofc.acmeplex.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "theatres")
@Entity
public class Theatre extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "location", nullable = false)
    private String location;

    @JsonIgnore
    @OneToMany(mappedBy = "theatre")
    private List<TheatreSeat> seats;

}
