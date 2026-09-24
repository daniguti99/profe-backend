package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "ciclo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciclo")
    private Integer id;

    @Column(name = "nombre")
    private String name;

    @OneToMany(mappedBy = "cycle", fetch = FetchType.LAZY)
    private List<CourseEntity> courses;
}
