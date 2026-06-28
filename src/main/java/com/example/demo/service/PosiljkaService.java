package com.example.demo.service;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.dto.impl.PosiljkaUpdateDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosiljkaService {

    private final PosiljkaRepository repository;
    private final PosiljkaMapper mapper;
    private final KorisnikService korisnikService;

    @Autowired
    public PosiljkaService(PosiljkaRepository repository, PosiljkaMapper mapper, KorisnikService korisnikService) {
        this.repository = repository;
        this.mapper = mapper;
        this.korisnikService = korisnikService;
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

    @Transactional
    public PosiljkaDto update(@Valid @NotNull PosiljkaUpdateDto p) {
        Posiljka trenutnaVerzija = repository.findLatestBySerijskiBroj(p.getSerijskiBroj())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Posiljka sa serijskim brojem " + p.getSerijskiBroj() + " ne postoji"
                ));

        Posiljka novaVerzija = new Posiljka();
        novaVerzija.setSerijskiBroj(trenutnaVerzija.getSerijskiBroj());
        novaVerzija.setUkupanIznos(trenutnaVerzija.getUkupanIznos());
        novaVerzija.setOpisSadrzaja(trenutnaVerzija.getOpisSadrzaja());
        novaVerzija.setKorisnik(trenutnaVerzija.getKorisnik());
        novaVerzija.setStatus(p.getStatus());
        novaVerzija.setNapomenaIzmene(p.getNapomenaIzmene());
        novaVerzija.setDatumIzmene();

        return mapper.toDto(repository.save(novaVerzija));
    }
}
