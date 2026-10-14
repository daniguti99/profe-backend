package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.Session;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para consultar y guardar sesiones.
 *
 * <p>La interfaz devuelve modelos de dominio ({@link Session} y {@link PublicSession}), nunca
 * entidades JPA: es la frontera que mantiene el dominio libre de dependencias de Spring, de
 * Hibernate y de {@code jakarta.persistence}. El adaptador de persistencia es quien traduce entidad
 * a dominio.</p>
 *
 * <p>El filtrado por usuario forma parte del propio contrato del puerto, y no de una decisión del
 * servicio ni de un detalle de la consulta. Exigir que toda lectura del usuario vaya por
 * {@code findByUserId(userId)} es la garantía de que solo se devuelven las sesiones del usuario
 * indicado y nunca las de otro. El identificador es de tipo {@code Integer} porque es el tipo de
 * {@code Session.userId} y de la columna {@code id_usuario}; la conversión desde el {@code Long} del
 * token se realiza en el servicio.</p>
 *
 * <p>Las tres operaciones públicas ({@code findPublicSessions}, {@code findPublicById} y
 * {@code incrementViews}) devuelven {@link PublicSession} porque necesitan, además de los campos de
 * la sesión, los datos básicos del autor. La ordenación se pide con un enum de dominio
 * ({@link PublicSessionSort}); traducirla a una consulta concreta es responsabilidad del
 * adaptador.</p>
 */
public interface SessionRepositoryPort {
    List<Session> findByUserId(Integer userId);
    Optional<Session> findById(Integer id);
    List<Session> findByTeachingUnitId(Integer teachingUnitId);
    Session save(Session session);

    /**
     * Devuelve las sesiones públicas y activas de cualquier autor, en el orden indicado.
     *
     * @param sort criterio de ordenación; nunca nulo
     * @return lista de sesiones públicas con los datos de su autor; vacía si no hay ninguna
     */
    List<PublicSession> findPublicSessions(PublicSessionSort sort);

    /**
     * Devuelve una sesión pública y activa por su identificador, con los datos de su autor.
     *
     * @param id identificador de la sesión
     * @return la sesión, o vacío si no existe, es privada o está inactiva
     */
    Optional<PublicSession> findPublicById(Integer id);

    /**
     * Incrementa en uno el contador de visualizaciones de una sesión.
     *
     * @param id identificador de la sesión
     */
    void incrementViews(Integer id);
}
