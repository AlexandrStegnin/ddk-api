package com.ddkolesnik.ddkapi.repository.bitrix;

import com.ddkolesnik.ddkapi.model.bitrix.BitrixContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface BitrixContactRepository extends JpaRepository<BitrixContact, String> {

    List<BitrixContact> findByCode(String partnerCode);

}
