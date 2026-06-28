package com.example.demo.service;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.entity.Korisnik;
import com.example.demo.entity.Posiljka;
import com.example.demo.entity.StatusPosiljke;
import com.example.demo.mapper.impl.PosiljkaMapper;
import com.example.demo.repository.impl.KorisnikRepository;
import com.example.demo.repository.impl.PosiljkaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PosiljkaService {

    private final PosiljkaRepository repository;
    private final KorisnikRepository korisnikRepository;
    private final PosiljkaMapper mapper;
    private final KorisnikService korisnikService;

    @Autowired
    public PosiljkaService(PosiljkaRepository repository, KorisnikRepository korisnikRepository, PosiljkaMapper mapper, KorisnikService korisnikService, KorisnikService korisnikService1) {
        this.repository = repository;
        this.korisnikRepository = korisnikRepository;
        this.mapper = mapper;
        this.korisnikService = korisnikService1;

    }
    @Transactional
    public List<PosiljkaDto> findAll(){
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<PosiljkaDto> findHistory(String serijskiBroj){
        return repository.findHistoryBySerijskiBroj(serijskiBroj).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PosiljkaDto create(@Valid @NotNull PosiljkaDto p) {
        Posiljka posiljka = mapper.toEntity(p);
        Korisnik korisnik = korisnikService.save(posiljka.getKorisnik());

        posiljka.setId(null);
        posiljka.setKorisnik(korisnik);
        posiljka.setStatus(StatusPosiljke.KREIRANA);
        posiljka.setDatumIzmene();

        repository.save(posiljka);
        return mapper.toDto(posiljka);
    }

    public PosiljkaDto update(@Valid @NotNull PosiljkaDto p) {
        
    }
}
