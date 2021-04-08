package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface MoneyRepository extends JpaRepository<Money, Long>, JpaSpecificationExecutor<Money> {

    Money findByTransactionUUID(String uuid);

    Long countByInvestorIdAndDateClosingIsNull(Long investorId);

    void deleteByTransactionUUID(String transactionUUID);

    @Query("SELECT m FROM Money m WHERE m.dateGiven = :dateGiven AND m.givenCash = :givenCash AND " +
            "m.facility.fullName = :facilityName AND m.cashSource.organization = :organizationId AND m.investor.login = :login")
    Money findMoney(@Param("dateGiven") LocalDate dateGiven, @Param("givenCash") BigDecimal givenCash,
                    @Param("facilityName") String facilityName, @Param("organizationId") String organizationId,
                    @Param("login") String login);

    @Query("SELECT m FROM Money m WHERE m.dateGiven = :dateGiven AND m.givenCash BETWEEN :cashFrom AND :cashTo AND " +
            "m.facility.fullName = :facilityName AND m.cashSource.organization = :organizationId AND m.investor.login = :login " +
            "AND m.dateClosing IS NULL AND m.typeClosingId IS NULL")
    Money findMoneyAround(@Param("dateGiven") LocalDate dateGiven, @Param("cashFrom") BigDecimal from, @Param("cashTo") BigDecimal to,
                    @Param("facilityName") String facilityName, @Param("organizationId") String organizationId,
                    @Param("login") String login);

    Money findBySourceMoneyId(Long sourceMoneyId);
}
