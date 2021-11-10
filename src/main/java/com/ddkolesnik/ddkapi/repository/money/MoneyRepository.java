package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface MoneyRepository extends JpaRepository<Money, Long>, JpaSpecificationExecutor<Money> {

  List<Money> findByTransactionUUID(String uuid);

  Long countByInvestorIdAndDateClosingIsNull(Long investorId);

  void deleteByTransactionUUID(String transactionUUID);

  @Query("SELECT m FROM Money m WHERE m.dateGiven = :dateGiven AND m.givenCash = :givenCash AND " +
      "m.facility.fullName = :facilityName AND m.cashSource.organization = :organizationId AND m.investor.login = :login " +
      "AND m.transactionUUID IS NULL")
  Money findMoney(@Param("dateGiven") LocalDate dateGiven, @Param("givenCash") BigDecimal givenCash,
                  @Param("facilityName") String facilityName, @Param("organizationId") String organizationId,
                  @Param("login") String login);

  @Query("SELECT m FROM Money m " +
      "WHERE m.investor.login = :login " +
      "AND m.facility.fullName = :facility " +
      "AND m.typeClosingId IS NULL AND m.dateClosing IS NULL " +
      "ORDER BY m.givenCash DESC ")
  List<Money> getMoniesByInvestorAndFacility(@Param("login") String login, @Param("facility") String facility);

  List<Money> findBySource(String source);
}
