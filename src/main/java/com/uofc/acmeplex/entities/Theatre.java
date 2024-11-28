package com.uofc.acmeplex.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Data
@Table(name = "theatres")
@Entity
public class Theatre extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "location", nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(name = "MOVIE_FK")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Cascade(CascadeType.ALL)
    private Movie movie;

    @OneToMany(mappedBy = "theatre")
    private List<TheatreSeat> seats;

}
