package com.example.demo.controller;

import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.service.PosiljkaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
