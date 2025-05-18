package com.lta.auditoriumsystem.Repositorios;

import com.lta.auditoriumsystem.Entities.Testimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonioRepository extends JpaRepository<Testimonio, Long> {
}
