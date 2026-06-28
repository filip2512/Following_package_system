package com.example.demo.controller;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.dto.impl.PosiljkaUpdateDto;
import com.example.demo.entity.StatusPosiljke;
import com.example.demo.service.PosiljkaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/posiljka")
public class PosiljkaController {

    private final PosiljkaService service;

    public PosiljkaController(PosiljkaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PosiljkaDto>> getAll(){
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/history/{serijskiBroj}")
    public ResponseEntity<List<PosiljkaDto>> getOnePackageHistory(@PathVariable String serijskiBroj){
        return new ResponseEntity<>(service.findHistory(serijskiBroj),HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PosiljkaDto>> filter(
            @RequestParam(required = false) Long korisnikId,
            @RequestParam(required = false) StatusPosiljke status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumKreiranja
    ) {
        return new ResponseEntity<>(service.filter(korisnikId, status, datumKreiranja), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PosiljkaDto> addPosiljka(@Valid @RequestBody @NotNull PosiljkaDto p){
        PosiljkaDto saved = service.create(p);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PosiljkaDto>> importFile(@RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(service.importFile(file), HttpStatus.CREATED);
    }

    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PosiljkaDto>> importCsv(@RequestParam("file") MultipartFile file) {
        return importFile(file);
    }

    @PutMapping
    public ResponseEntity<PosiljkaDto> updatePosiljka(@Valid @RequestBody @NotNull PosiljkaUpdateDto p){
        PosiljkaDto saved = service.update(p);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
