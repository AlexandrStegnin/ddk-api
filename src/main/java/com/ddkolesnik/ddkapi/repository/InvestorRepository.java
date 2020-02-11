package com.ddkolesnik.ddkapi.repository;

import com.ddkolesnik.ddkapi.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
