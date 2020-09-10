package com.ddkolesnik.ddkapi.service.log;

import com.ddkolesnik.ddkapi.model.log.TransactionLog;
import com.ddkolesnik.ddkapi.model.log.TransactionType;
import com.ddkolesnik.ddkapi.model.money.Money;
import com.ddkolesnik.ddkapi.repository.log.TransactionLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionLogService {

    TransactionLogRepository transactionLogRepository;

    InvestorCashLogService investorCashLogService;

    /**
     * Создать запись в логе по операции создания денег инвестора
     *
     * @param cash деньги инвестора
     */
    public void create(Money cash) {
        TransactionLog log = new TransactionLog();
        log.setInvestorsCashes(Collections.singleton(cash));
        log.setType(TransactionType.CREATE);
        log.setRollbackEnabled(true);
        create(log);
    }

    /**
     * Найти список транзакций, которые содержат переданные деньги инвестора
     *
     * @param cash деньги инвестора
     * @return список транзакций
     */
    public List<TransactionLog> findByCash(Money cash) {
        return transactionLogRepository.findByInvestorsCashesContains(cash);
    }

    /**
     * Создать запись об операции с деньгами
     *
     * @param transactionLog - операция
     */
    public void create(TransactionLog transactionLog) {
        transactionLogRepository.save(transactionLog);
    }

    /**
     * Обновить запись о транзакции
     *
     * @param log запись
     */
    public void update(TransactionLog log) {
        transactionLogRepository.save(log);
    }

    /**
     * Создать запись категории обновление в логе
     *
     * @param cash деньги инвестора
     */
    public void update(Money cash) {
        TransactionLog log = new TransactionLog();
        log.setInvestorsCashes(Collections.singleton(cash));
        log.setType(TransactionType.UPDATE);
        log.setRollbackEnabled(true);
        create(log);
        investorCashLogService.create(cash, log);
        blockLinkedLogs(cash, log);
    }

    /**
     * Метод для блокирования отката операций, если в них участвовала сумма инвестора
     *
     * @param cash сумма инвестора
     * @param log текущая операция логирования
     */
    private void blockLinkedLogs(Money cash, TransactionLog log) {
        List<TransactionLog> linkedLogs = findByCash(cash);
        linkedLogs.forEach(linkedLog -> {
            if (null == linkedLog.getBlockedFrom()) {
                if (!linkedLog.getId().equals(log.getId())) {
                    linkedLog.setRollbackEnabled(false);
                    linkedLog.setBlockedFrom(log);
                    update(linkedLog);
                }
            }
        });
    }

    /**
     * Разблокировать транзакции по id лога
     *
     * @param logId id лога
     */
    public void unblockTransactions(Long logId) {
        List<TransactionLog> blockedLogs = transactionLogRepository.findByBlockedFromId(logId);
        blockedLogs.forEach(blockedLog -> {
            blockedLog.setRollbackEnabled(true);
            blockedLog.setBlockedFrom(null);
            transactionLogRepository.save(blockedLog);
        });
    }

    /**
     * Удаление записей логов
     *
     * @param logs логи для удаления
     */
    public void delete(List<TransactionLog> logs) {
        transactionLogRepository.deleteAll(logs);
    }
}
