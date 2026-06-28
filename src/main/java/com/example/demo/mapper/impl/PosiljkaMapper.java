package com.example.demo.mapper.impl;

import com.example.demo.dto.impl.KorisnikDto;
import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.entity.Korisnik;
import com.example.demo.entity.Posiljka;
import com.example.demo.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class PosiljkaMapper implements DtoEntityMapper<PosiljkaDto, Posiljka> {
    private final KorisnikMapper korisnikMapper;

    public PosiljkaMapper(KorisnikMapper korisnikMapper) {
        this.korisnikMapper = korisnikMapper;
    }

    @Override
    public PosiljkaDto toDto(Posiljka posiljka) {
        if (posiljka == null) {
            return null;
        }

        KorisnikDto korisnikDto = korisnikMapper.toDto(posiljka.getKorisnik());

        return new PosiljkaDto(
                posiljka.getId(),
                posiljka.getSerijskiBroj(),
                posiljka.getUkupanIznos(),
                posiljka.getDatumIzmene(),
                posiljka.getStatus(),
                posiljka.getOpisSadrzaja(),
                posiljka.getNapomenaIzmene(),
                korisnikDto
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

        posiljka.setKorisnik(korisnikMapper.toEntity(posiljkaDto.getKorisnik()));

        return posiljka;
    }

}
