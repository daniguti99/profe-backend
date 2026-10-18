package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Página de sesiones públicas.
 *
 * <p>Modelo de dominio que reúne una porción del catálogo público ({@code sessions}) con la
 * información de si quedan más sesiones por devolver ({@code hasMore}). Existe para que el puerto
 * de salida pueda paginar sin arrastrar {@code Page}/{@code Slice} de Spring Data hasta el dominio:
 * el adaptador de persistencia es el único que conoce esos tipos y traduce a este modelo.</p>
 *
 * <p>No incluye el total de sesiones ni de páginas: el scroll infinito solo necesita saber si hay
 * una página más.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicSessionPage {

    /** Sesiones de la página solicitada; nunca vacío como {@code null}. */
    private List<PublicSession> sessions;

    /** {@code true} si existe al menos una sesión más allá de las devueltas. */
    private boolean hasMore;
}
