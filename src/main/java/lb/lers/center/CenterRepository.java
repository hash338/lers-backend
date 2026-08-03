package lb.lers.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CenterRepository extends JpaRepository<Center, UUID> {

    List<Center> findByStatus(String status);

    boolean existsByCode(String code);

    @Query(value = """
        SELECT *, (6371 * acos(
          cos(radians(:lat)) * cos(radians(latitude))
          * cos(radians(longitude) - radians(:lng))
          + sin(radians(:lat)) * sin(radians(latitude))
        )) AS dist
        FROM centers WHERE status = 'active'
        ORDER BY dist LIMIT 1
        """, nativeQuery = true)
    Center findNearest(@Param("lat") java.math.BigDecimal lat, @Param("lng") java.math.BigDecimal lng);

    @Query(value = """
        SELECT *, (6371 * acos(
          cos(radians(:lat)) * cos(radians(latitude))
          * cos(radians(longitude) - radians(:lng))
          + sin(radians(:lat)) * sin(radians(latitude))
        )) AS dist
        FROM centers WHERE status = 'active' AND id != :excludeId
        ORDER BY dist LIMIT 3
        """, nativeQuery = true)
    List<Center> findNearestExcluding(
            @Param("lat") java.math.BigDecimal lat,
            @Param("lng") java.math.BigDecimal lng,
            @Param("excludeId") UUID excludeId
    );
}