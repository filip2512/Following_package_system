package com.example.demo.mapper.impl;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.entity.Korisnik;
import com.example.demo.entity.Posiljka;
import com.example.demo.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class PosiljkaMapper implements DtoEntityMapper<PosiljkaDto, Posiljka> {

    @Override
    public PosiljkaDto toDto(Posiljka posiljka) {
        if (posiljka == null) {
            return null;
        }

        Long korisnikId = posiljka.getKorisnik() == null ? null : posiljka.getKorisnik().getId();

        return new PosiljkaDto(
                posiljka.getId(),
                posiljka.getSerijskiBroj(),
                posiljka.getUkupanIznos(),
                posiljka.getDatumIzmene(),
                posiljka.getStatus(),
                posiljka.getOpisSadrzaja(),
                posiljka.getNapomenaIzmene(),
                korisnikId
        );
    }

    @Override
    public Posiljka toEntity(PosiljkaDto posiljkaDto) {
        if (posiljkaDto == null) {
            return null;
        }

        Posiljka posiljka = new Posiljka();
        posiljka.setId(posiljkaDto.getId());
        posiljka.setSerijskiBroj(posiljkaDto.getSerijskiBroj());
        posiljka.setUkupanIznos(posiljkaDto.getUkupanIznos());
        posiljka.setDatumIzmene(posiljkaDto.getDatumIzmene());
        posiljka.setStatus(posiljkaDto.getStatus());
        posiljka.setOpisSadrzaja(posiljkaDto.getOpisSadrzaja());
        posiljka.setNapomenaIzmene(posiljkaDto.getNapomenaIzmene());

        if (posiljkaDto.getKorisnikId() != null) {
            Korisnik korisnik = new Korisnik();
            korisnik.setId(posiljkaDto.getKorisnikId());
            posiljka.setKorisnik(korisnik);
        }

        return posiljka;
    }
}
