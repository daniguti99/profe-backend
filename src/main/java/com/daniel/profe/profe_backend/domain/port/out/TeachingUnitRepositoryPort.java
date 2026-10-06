package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.TeachingUnit;

import java.util.List;

/**
 * Puerto de salida para consultar las unidades didácticas de un usuario.
 *
 * <p>La interfaz devuelve modelos de dominio ({@link TeachingUnit}), nunca entidades JPA: es la
 * frontera que mantiene el dominio libre de dependencias de Spring, de Hibernate y de
 * {@code jakarta.persistence}. El adaptador de persistencia es quien traduce entidad a dominio.</p>
 *
 * <p>El filtrado por usuario forma parte del propio contrato del puerto, y no de una decisión del
 * servicio ni de un detalle de la consulta. Exigir que toda lectura vaya por
 * {@code findByUserId(userId)} es la garantía de que solo se devuelven las unidades didácticas del
 * usuario indicado y nunca las de otro (RF-2). El identificador es de tipo {@code Integer} porque
 * es el tipo de {@code TeachingUnit.userId} y de la columna {@code id_usuario}; la conversión desde
 * el {@code Long} del token se realiza en el servicio.</p>
 */
public interface TeachingUnitRepositoryPort {
    List<TeachingUnit> findByUserId(Integer userId);
}
