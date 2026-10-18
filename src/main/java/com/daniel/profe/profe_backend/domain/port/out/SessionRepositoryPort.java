package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionPage;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.Resource;
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
 * <p>Las operaciones públicas de lectura ({@code findPublicSessions} y {@code findPublicById})
 * devuelven {@link PublicSession} (o una página de ellos, {@link PublicSessionPage}) porque
 * necesitan, además de los campos de la sesión, los datos básicos del autor. La ordenación se pide
 * con un enum de dominio ({@link PublicSessionSort}); traducirla a una consulta concreta es
 * responsabilidad del adaptador.</p>
 */
public interface SessionRepositoryPort {
    List<Session> findByUserId(Integer userId);
    Optional<Session> findById(Integer id);
    List<Session> findByTeachingUnitId(Integer teachingUnitId);
    Session save(Session session);

    /**
     * Devuelve una página de sesiones públicas y activas de cualquier autor, en el orden indicado.
     *
     * @param sort criterio de ordenación; nunca nulo
     * @param page página solicitada (base 0); nunca negativa
     * @param size número de sesiones por página (1..50)
     * @return página con las sesiones y si quedan más por devolver; sin sesiones si la página está
     *         más allá del final
     */
    PublicSessionPage findPublicSessions(PublicSessionSort sort, int page, int size);

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

    /**
     * Incrementa en uno el contador de descargas de una sesión.
     *
     * @param id identificador de la sesión
     */
    void incrementDownloads(Integer id);

    /**
     * Devuelve los recursos asociados a una sesión.
     *
     * @param sessionId identificador de la sesión
     * @return lista de recursos; vacía si la sesión no tiene ninguno
     */
    List<Resource> findResourcesBySessionId(Integer sessionId);
}
