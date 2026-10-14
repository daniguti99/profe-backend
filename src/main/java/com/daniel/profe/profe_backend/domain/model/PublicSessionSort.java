package com.daniel.profe.profe_backend.domain.model;

/**
 * Criterios de ordenación del listado de sesiones públicas.
 *
 * <p>Es un concepto de dominio porque expresa cómo quiere el usuario ver el catálogo compartido. La
 * traducción a una ordenación concreta de la base de datos no vive aquí, sino en el adaptador de
 * persistencia, que es quien conoce SQL.</p>
 */
public enum PublicSessionSort {

    /** Publicación más reciente primero (valor por defecto). */
    RECENT,

    /** Publicación más antigua primero. */
    OLDEST,

    /** Más descargas primero. */
    DOWNLOADS,

    /** Más visualizaciones primero. */
    VIEWS
}
