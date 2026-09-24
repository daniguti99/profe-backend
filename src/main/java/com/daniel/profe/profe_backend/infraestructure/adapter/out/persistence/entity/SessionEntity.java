package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sesion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Integer id;

    @Column(name = "titulo")
    private String title;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "materiales")
    private String materials;

    @Column(name = "duracion_total")
    private String totalDuration;

    @Column(name = "fecha")
    private LocalDate date;

    @Column(name = "calentamiento_tiempo")
    private String warmUpTime;

    @Column(name = "calentamiento_descripcion")
    private String warmUpDescription;

    @Column(name = "calentamiento_grafica_url")
    private String warmUpGraphicUrl;

    @Column(name = "calentamiento_observaciones")
    private String warmUpObservations;

    @Column(name = "parte_principal_tiempo")
    private String mainPartTime;

    @Column(name = "parte_principal_descripcion")
    private String mainPartDescription;

    @Column(name = "parte_principal_grafica_url")
    private String mainPartGraphicUrl;

    @Column(name = "parte_principal_observaciones")
    private String mainPartObservations;

    @Column(name = "vuelta_calma_tiempo")
    private String coolDownTime;

    @Column(name = "vuelta_calma_descripcion")
    private String coolDownDescription;

    @Column(name = "vuelta_calma_grafica_url")
    private String coolDownGraphicUrl;

    @Column(name = "vuelta_calma_observaciones")
    private String coolDownObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad")
    private TeachingUnitEntity teachingUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private UserEntity user;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<ResourceEntity> resources;


}
