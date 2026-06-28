package com.example.demo.dto.impl;

import com.example.demo.entity.StatusPosiljke;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PosiljkaUpdateDto {

    @NotBlank(message = "serijski broj je obavezan")
    @Size(min = 3, max = 50, message = "Serijski broj mora imati izmedju 3 i 50 karaktera")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Serijski broj moze sadrzati samo slova, brojeve i crticu")
    private String serijskiBroj;

    @NotNull(message = "status je obavezan")
    private StatusPosiljke status;

    @Size(max = 1000, message = "Napomena izmene moze imati najvise 1000 karaktera")
    private String napomenaIzmene;

    public PosiljkaUpdateDto() {
    }

    public PosiljkaUpdateDto(String serijskiBroj, StatusPosiljke status, String napomenaIzmene) {
        this.serijskiBroj = serijskiBroj;
        this.status = status;
        this.napomenaIzmene = napomenaIzmene;
    }

    public String getSerijskiBroj() {
        return serijskiBroj;
    }

    public void setSerijskiBroj(String serijskiBroj) {
        this.serijskiBroj = serijskiBroj;
    }

    public StatusPosiljke getStatus() {
        return status;
    }

    public void setStatus(StatusPosiljke status) {
        this.status = status;
    }

    public String getNapomenaIzmene() {
        return napomenaIzmene;
    }

    public void setNapomenaIzmene(String napomenaIzmene) {
        this.napomenaIzmene = napomenaIzmene;
    }
}
