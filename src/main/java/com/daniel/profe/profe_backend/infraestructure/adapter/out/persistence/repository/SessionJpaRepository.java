package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository;

import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionJpaRepository extends JpaRepository<SessionEntity, Integer> {

    /**
     * Devuelve las sesiones del usuario indicado, con las relaciones
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
     * <p>El orden lo fija el {@code ORDER BY} de SQL y no un {@code sorted()} en Java, para que los
     * {@code map} y {@code filter} posteriores no puedan desordenar el resultado.
     */
    @Query("""
        SELECT s FROM SessionEntity s
        JOIN FETCH s.teachingUnit
        JOIN FETCH s.user
        WHERE s.user.id = :userId
        ORDER BY s.id ASC
        """)
    List<SessionEntity> findByUserIdWithRelations(@Param("userId") Integer userId);
}
