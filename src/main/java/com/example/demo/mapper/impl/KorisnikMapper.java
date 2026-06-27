package com.example.demo.mapper.impl;

import com.example.demo.dto.impl.KorisnikDto;
import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.entity.Korisnik;
import com.example.demo.entity.Posiljka;
import com.example.demo.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KorisnikMapper implements DtoEntityMapper<KorisnikDto, Korisnik> {

    private final PosiljkaMapper posiljkaMapper;

    public KorisnikMapper(PosiljkaMapper posiljkaMapper) {
        this.posiljkaMapper = posiljkaMapper;
    }

    @Override
    public KorisnikDto toDto(Korisnik korisnik) {
        if (korisnik == null) {
            return null;
        }

        List<PosiljkaDto> posiljke = korisnik.getPosiljke()
                .stream()
                .map(posiljkaMapper::toDto)
                .toList();

        return new KorisnikDto(
                korisnik.getId(),
                korisnik.getIme(),
                korisnik.getJmbg(),
                korisnik.getAdresa(),
                posiljke
        );
    }

    @Override
    public Korisnik toEntity(KorisnikDto korisnikDto) {
        if (korisnikDto == null) {
            return null;
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setId(korisnikDto.getId());
        korisnik.setIme(korisnikDto.getIme());
        korisnik.setJmbg(korisnikDto.getJmbg());
        korisnik.setAdresa(korisnikDto.getAdresa());

        List<Posiljka> posiljke = korisnikDto.getPosiljke()
                .stream()
                .map(posiljkaMapper::toEntity)
                .toList();

        posiljke.forEach(posiljka -> posiljka.setKorisnik(korisnik));
        korisnik.setPosiljke(posiljke);

        return korisnik;
    }
}
