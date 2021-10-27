package com.ddkolesnik.ddkapi.model.log;

/**
 * @author Alexandr Stegnin
 */

public enum CashType {

  NEW(1, "Новая сумма"),
  OLD(2, "Старая сумма"),
  UNDEFINED(0, "Не определено"),
  INVESTOR_CASH(3, "Деньги инвесторов"),
  SALE_CASH(4, "Деньги с продажи"),
  RENT_CASH(5, "Деньги с аренды"),
  CASH_1C(6, "Проводка из 1С"),
  INVESTMENT_BODY(7, "Тело инвестиций"),
  CASH_1C_COMMISSION(8, "Проводка из 1С. Комиссия"),
  CASH_1C_CASHING(9, "Проводка из 1С. Вывод"),
  RESALE_SHARE(10, "Перепродажа доли"),
  RE_BUY_SHARE(11, "Перепокупка доли"),
  CORRECTION(12, "Корректировка");

  private final int id;

  private final String title;

  CashType(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public static CashType fromTitle(String title) {
    for (CashType value : values()) {
      if (value.title.equalsIgnoreCase(title)) {
        return value;
      }
    }
    return UNDEFINED;
  }

}
