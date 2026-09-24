package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "unidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Integer id;

    @Column(name = "titulo")
    private String title;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "temporalizacion")
    private String schedule;

    @Column(name = "notas")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso")
    private CourseEntity course;

    @OneToMany(mappedBy = "teachingUnit", fetch = FetchType.LAZY)
    private List<SessionEntity> sessions;

    @OneToMany(mappedBy = "teachingUnit", fetch = FetchType.LAZY)
    private List<TeachingUnitTagEntity> teachingUnitTags;
}
