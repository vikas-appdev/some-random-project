package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.Person;

import java.util.Collection;

public interface PersonCRUDRepository <T extends Person>{
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);
}
