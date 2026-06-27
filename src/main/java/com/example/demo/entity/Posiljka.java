package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "posiljka")
public class Posiljka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serijskiBroj;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal ukupanIznos;

    @Column(nullable = false)
    private LocalDateTime datumIzmene;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPosiljke status;

    @Column(nullable = false, length = 1000)
    private String opisSadrzaja;

    @Column(length = 1000, nullable= true)
    private String napomenaIzmene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_korisnika", nullable = false)
    private Korisnik korisnik;

    public Posiljka() {
    }

    public Posiljka(String serijskiBroj, BigDecimal ukupanIznos, StatusPosiljke status,
                   String opisSadrzaja, String napomenaIzmene, Korisnik korisnik) {
        this.serijskiBroj = serijskiBroj;
        this.ukupanIznos = ukupanIznos;
        this.status = status;
        this.opisSadrzaja = opisSadrzaja;
        this.napomenaIzmene = napomenaIzmene;
        this.korisnik = korisnik;
    }

    @PrePersist
    public void prePersist() {
        if (datumIzmene == null) {
            datumIzmene = LocalDateTime.now();
        }

        if (status == null) {
            status = StatusPosiljke.KREIRANA;
        }
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

    public void setDatumIzmene() {this.datumIzmene = LocalDateTime.now();}

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

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }
}
