package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de la tabla {@code sesion_descarga}: registra qué usuario ha descargado qué sesión.
 *
 * <p>La restricción {@code UNIQUE (id_sesion, id_usuario)} de la tabla es la que impide registrar dos
 * veces la misma descarga y, con ello, contar dos veces al mismo usuario.</p>
 */
@Entity
@Table(name = "sesion_descarga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDownloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descarga")
    private Long id;

    @Column(name = "id_sesion", nullable = false)
    private Integer sessionId;

    @Column(name = "id_usuario", nullable = false)
    private Integer userId;

    @Column(name = "fecha_descarga", nullable = false)
    private LocalDateTime downloadDate;
}
