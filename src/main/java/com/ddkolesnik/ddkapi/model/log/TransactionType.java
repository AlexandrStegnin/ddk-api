package com.ddkolesnik.ddkapi.model.log;

/**
 * @author Alexandr Stegnin
 */

public enum TransactionType {

  CREATE(1, "Создание"),
  UPDATE(2, "Изменение"),
  DIVIDE(3, "Разделение"),
  REINVESTMENT(4, "Реинвестирование"),
  CLOSING(5, "Закрытие"),
  CLOSING_RESALE(6, "Закрытие. Перепродажа доли"),
  UNDEFINED(0, "Не определено"),
  CASHING(7, "Вывод"),
  CASHING_COMMISSION(8, "Вывод. Комиссия");

  private final int id;

  private final String title;

  TransactionType(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public static TransactionType fromTitle(String title) {
    for (TransactionType value : values()) {
      if (value.title.equalsIgnoreCase(title)) {
        return value;
      }
    }
    return UNDEFINED;
  }

}
