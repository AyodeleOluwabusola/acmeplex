package com.uofc.acmeplex.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id ;

    @JsonIgnore
    @CreationTimestamp
    @Column(name= "CREATE_DATE")
    private LocalDateTime createDate = LocalDateTime.now();

    @JsonIgnore
    @Column(name= "LAST_MODIFIED")
    private LocalDateTime lastModified;

    @JsonIgnore
    @Column(name= "ACTIVE")
    private Boolean active = true;

    @JsonIgnore
    @Column(name= "DELETED")
    private Boolean deleted = false;


    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.lastModified = this.createDate;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
