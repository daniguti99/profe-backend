package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository;

import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.TeachingUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeachingUnitJpaRepository extends JpaRepository<TeachingUnitEntity, Integer> {

    /**
     * Devuelve las unidades didácticas del usuario indicado, con las relaciones
     * {@code user} y {@code course} ya cargadas y en orden ascendente de identificador.
     *
     * <p>La consulta se escribe a mano en lugar de como método derivado porque solo así Spring Data
     * admite el {@code JOIN FETCH}: un método derivado cargaría las relaciones después, una a una
     * (N+1), y dejaría el orden repartido entre el nombre del método y el código Java.
     *
     * <p>El {@code JOIN FETCH} es interno, de modo que una unidad sin usuario o sin curso no llega
     * al resultado: la propia consulta es la fuente de verdad de que ambas relaciones existen. Por
     * eso no se cargan las colecciones de la entidad, que multiplicarían las filas devueltas.
     *
     * <p>El orden lo fija el {@code ORDER BY} de SQL y no un {@code sorted()} en Java, para que los
     * {@code map} y {@code filter} posteriores no puedan desordenar el resultado.
     */
    @Query("""
        SELECT tu FROM TeachingUnitEntity tu
        JOIN FETCH tu.user
        JOIN FETCH tu.course
        WHERE tu.user.id = :userId
        ORDER BY tu.id ASC
        """)
    List<TeachingUnitEntity> findByUserIdWithRelations(@Param("userId") Integer userId);
}