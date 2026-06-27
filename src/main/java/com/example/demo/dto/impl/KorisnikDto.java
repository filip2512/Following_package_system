package com.example.demo.dto.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class KorisnikDto {

    private Long id;

    @NotBlank(message = "ime je obavezno")
    private String ime;

    @NotBlank(message = "jmbg je obavezan")
    @Size(min = 13, max = 13, message = "jmbg ima 13 karaktera")
    private String jmbg;

    @NotBlank(message = "adresa je obavezna")
    private String adresa;

    private List<PosiljkaDto> posiljke = new ArrayList<>();

    public KorisnikDto() {
    }

    public KorisnikDto(Long id, String ime, String jmbg, String adresa) {
        this.id = id;
        this.ime = ime;
        this.jmbg = jmbg;
        this.adresa = adresa;
    }

    public KorisnikDto(Long id, String ime, String jmbg, String adresa, List<PosiljkaDto> posiljke) {
        this.id = id;
        this.ime = ime;
        this.jmbg = jmbg;
        this.adresa = adresa;
        this.posiljke = posiljke;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public List<PosiljkaDto> getPosiljke() {
        return posiljke;
    }

    public void setPosiljke(List<PosiljkaDto> posiljke) {
        this.posiljke = posiljke;
    }
}
