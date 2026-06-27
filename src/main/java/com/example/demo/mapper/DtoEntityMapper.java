package com.example.demo.mapper;

public interface DtoEntityMapper <T,E>{
    T toDto(E e);
    E toEntity(T t);
}
