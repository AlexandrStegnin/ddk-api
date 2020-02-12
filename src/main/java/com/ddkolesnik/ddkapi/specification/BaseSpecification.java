package com.ddkolesnik.ddkapi.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Alexandr Stegnin
 * Базовая спецификация
 * @param <T> класс-сущность для которой настраивается фильтрация
 * @param <U> класс-фильтр, который определяет поля для фильтрации
 */
public abstract class BaseSpecification<T, U> {

    public abstract Specification<T> getFilter(U request);

}
