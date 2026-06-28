package com.example.demo.controller;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.service.PosiljkaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ResponseEntity<PosiljkaDto> addPosiljka(@Valid @RequestBody @NotNull PosiljkaDto p){
        PosiljkaDto saved = service.create(p);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    @PutMapping
    public ResponseEntity<PosiljkaDto> updatePosiljka(@Valid @RequestBody @NotNull PosiljkaDto p){
        PosiljkaDto saved = service.update(p);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
