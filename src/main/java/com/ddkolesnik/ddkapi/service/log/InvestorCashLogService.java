package com.ddkolesnik.ddkapi.service.log;

import com.ddkolesnik.ddkapi.model.log.InvestorCashLog;
import com.ddkolesnik.ddkapi.model.log.TransactionLog;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.log.InvestorCashLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с историей операций над деньгами
 *
 * @author Alexandr Stegnin
 */

@Service
public class InvestorCashLogService {

    private final InvestorCashLogRepository investorCashLogRepository;

    public InvestorCashLogService(InvestorCashLogRepository investorCashLogRepository) {
        this.investorCashLogRepository = investorCashLogRepository;
    }

    /**
     * Найти сумму в истории по id
     *
     * @param id суммы
     * @return найденная сумма
     */
    public InvestorCashLog findById(Long id) {
        return investorCashLogRepository.findByCashId(id);
    }

    /**
     * Найти сумму в логе по id суммы
     *
     * @param cashId id суммы
     * @return найденная запись
     */
    public InvestorCashLog findByCashId(Long cashId) {
        return investorCashLogRepository.findByCashId(cashId);
    }

    /**
     * Создать сумму в истории и в логе на основании суммы инвестора
     *
     * @param cash сумма инвестора
     * @param log
     */
    public void create(Money cash, TransactionLog log) {
        InvestorCashLog cashLog = new InvestorCashLog(cash, log);
        investorCashLogRepository.save(cashLog);
    }

    /**
     * Создать суммы в истории и в логе на основании списка сумм
     *
     * @param cashes список денег
     */
    public void update(List<Money> cashes, TransactionLog log) {
        cashes.forEach(cash -> {
            InvestorCashLog cashLog = new InvestorCashLog(cash, log);
            investorCashLogRepository.save(cashLog);
        });
    }

    public void delete(Money cash) {
        InvestorCashLog cashLog = findById(cash.getId());
        if (null != cashLog) {
            investorCashLogRepository.delete(cashLog);
        }
    }

}
