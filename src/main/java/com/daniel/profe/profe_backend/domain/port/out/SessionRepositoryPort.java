package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.Session;

import java.util.List;

/**
 * Puerto de salida para consultar las sesiones de un usuario.
 *
 * <p>La interfaz devuelve modelos de dominio ({@link Session}), nunca entidades JPA: es la
 * frontera que mantiene el dominio libre de dependencias de Spring, de Hibernate y de
 * {@code jakarta.persistence}. El adaptador de persistencia es quien traduce entidad a dominio.</p>
 *
 * <p>El filtrado por usuario forma parte del propio contrato del puerto, y no de una decisión del
 * servicio ni de un detalle de la consulta. Exigir que toda lectura vaya por
 * {@code findByUserId(userId)} es la garantía de que solo se devuelven las sesiones del
 * usuario indicado y nunca las de otro. El identificador es de tipo {@code Integer} porque
 * es el tipo de {@code Session.userId} y de la columna {@code id_usuario}; la conversión desde
 * el {@code Long} del token se realiza en el servicio.</p>
 */
public interface SessionRepositoryPort {
    List<Session> findByUserId(Integer userId);
}
