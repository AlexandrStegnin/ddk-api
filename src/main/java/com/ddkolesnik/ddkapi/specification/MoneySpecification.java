package com.ddkolesnik.ddkapi.specification;

import com.ddkolesnik.ddkapi.model.money.Facility_;
import com.ddkolesnik.ddkapi.model.money.Investor_;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.model.money.Money_;
import com.ddkolesnik.ddkapi.specification.filter.MoneyFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.springframework.data.jpa.domain.Specification.where;

/**
 * @author Alexandr Stegnin
 *
 */

@Component
public class MoneySpecification extends BaseSpecification<Money, MoneyFilter> {

    @Override
    public Specification<Money> getFilter(MoneyFilter filter) {
        if (Objects.isNull(filter)) return null;
        return (root, query, cb) -> where(
                defaultConditions())
                .and(investorLoginEqual(filter.getInvestor()))
                .and(loginContainsPartnerCode(filter.getPartnerCode()))
                .and(dateGivenCashBetween(filter.getFromDate(), filter.getToDate()))
                .and(facilityEqual(filter.getFacility()))
                .toPredicate(root, query, cb);
    }

    private static Specification<Money> dateGivenCashBetween(LocalDate min, LocalDate max) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.nonNull(min) && Objects.nonNull(max)) {
                return criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Money_.dateGiven), min),
                        criteriaBuilder.lessThanOrEqualTo(root.get(Money_.dateGiven), max)
                );
            } else if (Objects.nonNull(min)) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Money_.dateGiven), min);
            } else if (Objects.nonNull(max)) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Money_.dateGiven), max);
            } else {
                return null;
            }
        }
        );
    }

    private static Specification<Money> facilityEqual(String facility) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.isNull(facility) || StringUtils.isEmpty(facility)) {
                return null;
            } else {
                return criteriaBuilder.equal(root.get(Money_.facility).get(Facility_.name), facility);
            }
        }
        );
    }

    private static Specification<Money> defaultConditions() {
        return ((root, query, cb) -> where(facilityIsNotNull())
                .and(givenCashGreaterThanZero())
                .and(notClosing())
                .toPredicate(root, query, cb)
        );
    }

    private static Specification<Money> investorLoginEqual(String login) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.isNull(login) || StringUtils.isEmpty(login)) {
                return null;
            } else {
                return criteriaBuilder.equal(root.get(Money_.investor).get(Investor_.login), login);
            }
        }
        );
    }

    private static Specification<Money> loginContainsPartnerCode(String partnerCode) {
        return investorLoginEqual(partnerCode);
    }

    private static Specification<Money> givenCashGreaterThanZero() {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.gt(root.get(Money_.givenCash), BigDecimal.ZERO)
        );
    }

    private static Specification<Money> facilityIsNotNull() {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get(Money_.facility))
        );
    }

    private static Specification<Money> notClosing() {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(Money_.dateClosing))
        );
    }
}
