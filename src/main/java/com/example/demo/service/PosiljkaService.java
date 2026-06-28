package com.example.demo.service;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.mapper.impl.PosiljkaMapper;
import com.example.demo.repository.impl.PosiljkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosiljkaService {

    private final PosiljkaRepository repository;
    private final PosiljkaMapper mapper;

    @Autowired
    public PosiljkaService(PosiljkaRepository repository, PosiljkaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public List<PosiljkaDto> findAll(){
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    public List<PosiljkaDto> findHistory(String serijskiBroj){
        return repository.findHistoryBySerijskiBroj(serijskiBroj).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

}
