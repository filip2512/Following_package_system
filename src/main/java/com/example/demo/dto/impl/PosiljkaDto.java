package com.example.demo.dto.impl;

import com.example.demo.entity.StatusPosiljke;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PosiljkaDto {

    private Long id;

    @NotBlank(message = "serijski broj je obavezan")
    @Size(min = 3, max = 50, message = "Serijski broj mora imati izmedju 3 i 50 karaktera")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Serijski broj moze sadrzati samo slova, brojeve i crticu")
    private String serijskiBroj;

    @Positive(message = "iznos mora da bude veci od 0")
    @NotNull(message = "iznos je obavezno polje")
    private BigDecimal ukupanIznos;

    private LocalDateTime datumIzmene;

    private StatusPosiljke status;

    @NotBlank(message = "opis je obavezan")
    @Size(min = 3, max = 1000, message = "Opis sadrzaja mora imati izmedju 3 i 1000 karaktera")
    private String opisSadrzaja;

    @Size(max = 1000, message = "Napomena izmene moze imati najvise 1000 karaktera")
    private String napomenaIzmene;

    @Valid
    @NotNull(message = "korisnik je obavezan")
    private KorisnikDto korisnik;

    public PosiljkaDto() {
    }

    public PosiljkaDto(Long id, String serijskiBroj, BigDecimal ukupanIznos, LocalDateTime datumIzmene,
                       StatusPosiljke status, String opisSadrzaja, String napomenaIzmene, KorisnikDto korisnik) {
        this.id = id;
        this.serijskiBroj = serijskiBroj;
        this.ukupanIznos = ukupanIznos;
        this.datumIzmene = datumIzmene;
        this.status = status;
        this.opisSadrzaja = opisSadrzaja;
        this.napomenaIzmene = napomenaIzmene;
        this.korisnik = korisnik;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerijskiBroj() {
        return serijskiBroj;
    }

    public void setSerijskiBroj(String serijskiBroj) {
        this.serijskiBroj = serijskiBroj;
    }

    public BigDecimal getUkupanIznos() {
        return ukupanIznos;
    }

    public void setUkupanIznos(BigDecimal ukupanIznos) {
        this.ukupanIznos = ukupanIznos;
    }

    public LocalDateTime getDatumIzmene() {
        return datumIzmene;
    }

    public void setDatumIzmene(LocalDateTime datumIzmene) {
        this.datumIzmene = datumIzmene;
    }

    public StatusPosiljke getStatus() {
        return status;
    }

    public void setStatus(StatusPosiljke status) {
        this.status = status;
    }

    public String getOpisSadrzaja() {
        return opisSadrzaja;
    }

    public void setOpisSadrzaja(String opisSadrzaja) {
        this.opisSadrzaja = opisSadrzaja;
    }

    public String getNapomenaIzmene() {
        return napomenaIzmene;
    }

    public void setNapomenaIzmene(String napomenaIzmene) {
        this.napomenaIzmene = napomenaIzmene;
    }

    public KorisnikDto getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(KorisnikDto korisnik) {
        this.korisnik = korisnik;
    }
}
