package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "etiqueta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etiqueta")
    private Integer id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "descripcion")
    private String description;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<TeachingUnitTagEntity> teachingUnitTags;
}
