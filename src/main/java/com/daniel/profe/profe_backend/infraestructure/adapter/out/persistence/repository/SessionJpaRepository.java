package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository;

import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.ResourceEntity;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionJpaRepository extends JpaRepository<SessionEntity, Integer> {

    /**
     * Devuelve las sesiones activas del usuario indicado, con las relaciones
     * {@code teachingUnit} y {@code user} ya cargadas y en orden ascendente de identificador.
     *
     * <p>La consulta se escribe a mano en lugar de como método derivado porque solo así Spring Data
     * admite el {@code JOIN FETCH}: un método derivado cargaría las relaciones después, una a una
     * (N+1), y dejaría el orden repartido entre el nombre del método y el código Java.
     *
     * <p>El {@code JOIN FETCH} es interno, de modo que una sesión sin unidad didáctica o sin usuario
     * no llega al resultado: la propia consulta es la fuente de verdad de que ambas relaciones
     * existen. Por eso no se cargan las colecciones de la entidad, que multiplicarían las filas
     * devueltas.
     *
     * <p>El filtro {@code s.activo = true} garantiza que solo se devuelven sesiones activas,
     * excluyendo las que han sido borradas lógicamente.
     *
     * <p>El orden lo fija el {@code ORDER BY} de SQL y no un {@code sorted()} en Java, para que los
     * {@code map} y {@code filter} posteriores no puedan desordenar el resultado.
     */
    @Query("""
        SELECT s FROM SessionEntity s
        JOIN FETCH s.teachingUnit
        JOIN FETCH s.user
        WHERE s.user.id = :userId
        AND s.active = true
        ORDER BY s.id ASC
        """)
    List<SessionEntity> findByUserIdWithRelations(@Param("userId") Integer userId);

    /**
     * Devuelve las sesiones activas de una unidad didáctica específica.
     * Se usa para la cascada de borrado lógico al desactivar una unidad.
     */
    @Query("""
        SELECT s FROM SessionEntity s
        WHERE s.teachingUnit.id = :teachingUnitId
        AND s.active = true
        ORDER BY s.id ASC
        """)
    List<SessionEntity> findByTeachingUnitId(@Param("teachingUnitId") Integer teachingUnitId);

    /**
     * Devuelve una página de sesiones públicas y activas de cualquier autor, con el autor ya
     * cargado, en el orden que indique el {@code Sort} recibido.
     *
     * <p>El {@code JOIN FETCH} interno garantiza que la sesión tiene autor y evita el N+1 al
     * construir la información del autor en el DTO. El {@code Sort} lo construye el adaptador, que
     * traduce el enum de dominio; aquí no se fija ningún {@code ORDER BY} fijo para no chocar con
     * él.</p>
     *
     * <p>Devuelve un {@code Slice} y no un {@code Page}: el scroll infinito solo necesita saber si
     * hay una página más ({@code hasNext()}), y un {@code Slice} lo resuelve pidiendo un elemento
     * de más sin ejecutar el {@code COUNT} que sí haría un {@code Page}.</p>
     */
    @Query("""
        SELECT s FROM SessionEntity s
        JOIN FETCH s.user
        WHERE s.isPublic = true
        AND s.active = true
        """)
    Slice<SessionEntity> findPublicSessions(Pageable pageable);

    /**
     * Devuelve una sesión pública y activa por su identificador, con el autor ya cargado.
     *
     * <p>El filtro {@code publico = true AND activo = true} es lo que convierte "no existe", "es
     * privada" y "está inactiva" en un único resultado vacío, para que el detalle público no
     * revele la existencia de sesiones privadas ajenas.</p>
     */
    @Query("""
        SELECT s FROM SessionEntity s
        JOIN FETCH s.user
        WHERE s.id = :id
        AND s.isPublic = true
        AND s.active = true
        """)
    Optional<SessionEntity> findPublicById(@Param("id") Integer id);

    /**
     * Incrementa en uno el contador de visualizaciones de una sesión.
     *
     * <p>Es un {@code UPDATE} atómico en lugar de leer, sumar y guardar: así dos visualizaciones
     * simultáneas no se pisan entre sí.</p>
     */
    @Modifying
    @Query("UPDATE SessionEntity s SET s.views = s.views + 1 WHERE s.id = :id")
    void incrementViews(@Param("id") Integer id);

    /**
     * Incrementa en uno el contador de descargas de una sesión.
     *
     * <p>Es un {@code UPDATE} atómico para que dos descargas simultáneas no se pisen entre sí.</p>
     */
    @Modifying
    @Query("UPDATE SessionEntity s SET s.downloads = s.downloads + 1 WHERE s.id = :id")
    void incrementDownloads(@Param("id") Integer id);

    /**
     * Devuelve los recursos de una sesión.
     */
    @Query("SELECT r FROM ResourceEntity r WHERE r.session.id = :sessionId")
    List<ResourceEntity> findResourcesBySessionId(@Param("sessionId") Integer sessionId);
}
