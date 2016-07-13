package org.jhipster.health.repository;

import org.jhipster.health.domain.Points;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the Points entity.
 */
@SuppressWarnings("unused")
public interface PointsRepository extends JpaRepository<Points,Long> {

    @Query("select points from Points points where points.user.login = ?#{principal.username} order by points.date desc")
    Page<Points> findAllForCurrentUserByOrderByDateDesc(Pageable pageble);

    Page<Points> findAllByOrderByDateDesc(Pageable pageable);
}
