package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<E,ID> {

    E save(E entity);

    List<E> saveAll(List<E> entities);

    Optional<E> findById(ID id);

    List<E> findAll();

    E update(E entity);
}
