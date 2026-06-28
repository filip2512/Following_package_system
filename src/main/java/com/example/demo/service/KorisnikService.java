package com.example.demo.service;

import com.example.demo.entity.Korisnik;
import com.example.demo.repository.impl.KorisnikRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class KorisnikService {
    private final KorisnikRepository korisnikRepository;

    public KorisnikService(KorisnikRepository korisnikRepository) {
        this.korisnikRepository = korisnikRepository;
    }

    public Korisnik save(Korisnik korisnik) {
        return korisnikRepository.findByJmbg(korisnik.getJmbg())
                .map(postojeciKorisnik -> update(postojeciKorisnik, korisnik))
                .orElseGet(() -> korisnikRepository.save(korisnik));
    }

    private Korisnik update(Korisnik postojeciKorisnik, Korisnik korisnikIzZahteva) {
        boolean izmenjen = false;

        if (!Objects.equals(postojeciKorisnik.getIme(), korisnikIzZahteva.getIme())) {
            postojeciKorisnik.setIme(korisnikIzZahteva.getIme());
            izmenjen = true;
        }

        if (!Objects.equals(postojeciKorisnik.getAdresa(), korisnikIzZahteva.getAdresa())) {
            postojeciKorisnik.setAdresa(korisnikIzZahteva.getAdresa());
            izmenjen = true;
        }

        if (izmenjen) {
            return korisnikRepository.update(postojeciKorisnik);
        }

        return postojeciKorisnik;
    }

}
