package com.example.demo.service;

import com.example.demo.dto.impl.KorisnikDto;
import com.example.demo.dto.impl.PosiljkaDto;
import com.example.demo.dto.impl.PosiljkaUpdateDto;
import com.example.demo.entity.Korisnik;
import com.example.demo.entity.Posiljka;
import com.example.demo.entity.StatusPosiljke;
import com.example.demo.mapper.impl.PosiljkaMapper;
import com.example.demo.repository.impl.PosiljkaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosiljkaService {

    private final PosiljkaRepository repository;
    private final PosiljkaMapper mapper;
    private final KorisnikService korisnikService;

    @Autowired
    public PosiljkaService(PosiljkaRepository repository, PosiljkaMapper mapper, KorisnikService korisnikService) {
        this.repository = repository;
        this.mapper = mapper;
        this.korisnikService = korisnikService;
    }
    @Transactional
    public List<PosiljkaDto> findAll(){
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<PosiljkaDto> findHistory(String serijskiBroj){
        return repository.findHistoryBySerijskiBroj(serijskiBroj).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PosiljkaDto> filter(Long korisnikId, StatusPosiljke status, LocalDate datumKreiranja) {
        LocalDateTime kreiranoOd = null;
        LocalDateTime kreiranoDo = null;

        if (datumKreiranja != null) {
            kreiranoOd = datumKreiranja.atStartOfDay();
            kreiranoDo = datumKreiranja.plusDays(1).atStartOfDay();
        }

        return repository.filterLatest(korisnikId, status, kreiranoOd, kreiranoDo).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PosiljkaDto create(@Valid @NotNull PosiljkaDto p) {
        Posiljka posiljka = mapper.toEntity(p);
        Korisnik korisnik = korisnikService.save(posiljka.getKorisnik());

        posiljka.setId(null);
        posiljka.setKorisnik(korisnik);
        posiljka.setStatus(StatusPosiljke.KREIRANA);
        posiljka.setDatumIzmene();

        repository.save(posiljka);
        return mapper.toDto(posiljka);
    }

    @Transactional
    public List<PosiljkaDto> importFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV ili XLSX fajl je obavezan");
        }

        List<PosiljkaDto> posiljke;
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();

        if (filename.endsWith(".csv")) {
            posiljke = parseCsv(file);
        } else if (filename.endsWith(".xlsx")) {
            posiljke = parseXlsx(file);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podrzani formati su .csv i .xlsx");
        }

        return posiljke.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    @Transactional
    public PosiljkaDto update(@Valid @NotNull PosiljkaUpdateDto p) {
        Posiljka trenutnaVerzija = repository.findLatestBySerijskiBroj(p.getSerijskiBroj())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Posiljka sa serijskim brojem " + p.getSerijskiBroj() + " ne postoji"
                ));

        Posiljka novaVerzija = new Posiljka();
        novaVerzija.setSerijskiBroj(trenutnaVerzija.getSerijskiBroj());
        novaVerzija.setUkupanIznos(trenutnaVerzija.getUkupanIznos());
        novaVerzija.setOpisSadrzaja(trenutnaVerzija.getOpisSadrzaja());
        novaVerzija.setKorisnik(trenutnaVerzija.getKorisnik());
        novaVerzija.setStatus(p.getStatus());
        novaVerzija.setNapomenaIzmene(p.getNapomenaIzmene());
        novaVerzija.setDatumIzmene();

        return mapper.toDto(repository.save(novaVerzija));
    }

    private List<PosiljkaDto> parseCsv(MultipartFile file) {
        List<PosiljkaDto> posiljke = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null || header.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV fajl nema header red");
            }

            validateHeader(header);

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                posiljke.add(parseCsvRow(line, lineNumber));
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV fajl ne moze da se procita", e);
        }

        if (posiljke.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV fajl nema podatke za import");
        }

        return posiljke;
    }

    private List<PosiljkaDto> parseXlsx(MultipartFile file) {
        List<PosiljkaDto> posiljke = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XLSX fajl nema header red");
            }

            validateHeader(toHeader(headerRow, formatter));

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isBlankRow(row, formatter)) {
                    continue;
                }

                posiljke.add(parseSpreadsheetRow(row, formatter, rowIndex + 1));
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XLSX fajl ne moze da se procita", e);
        }

        if (posiljke.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XLSX fajl nema podatke za import");
        }

        return posiljke;
    }

    private void validateHeader(String header) {
        String normalizedHeader = header.trim();
        String expectedHeader = "serijskiBroj,ukupanIznos,opisSadrzaja,napomenaIzmene,ime,jmbg,adresa";

        if (!expectedHeader.equals(normalizedHeader)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CSV header mora biti: " + expectedHeader
            );
        }
    }

    private PosiljkaDto parseCsvRow(String line, int lineNumber) {
        List<String> columns = splitCsvLine(line);

        if (columns.size() != 7) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Red " + lineNumber + " mora imati 7 kolona"
            );
        }

        return toPosiljkaDto(columns, lineNumber);
    }

    private PosiljkaDto parseSpreadsheetRow(Row row, DataFormatter formatter, int lineNumber) {
        List<String> columns = new ArrayList<>();

        for (int cellIndex = 0; cellIndex < 7; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            columns.add(formatSpreadsheetCell(cell, formatter, cellIndex).trim());
        }

        return toPosiljkaDto(columns, lineNumber);
    }

    private PosiljkaDto toPosiljkaDto(List<String> columns, int lineNumber) {
        try {
            KorisnikDto korisnik = new KorisnikDto(
                    null,
                    columns.get(4).trim(),
                    columns.get(5).trim(),
                    columns.get(6).trim()
            );

            return new PosiljkaDto(
                    null,
                    columns.get(0).trim(),
                    new BigDecimal(columns.get(1).trim()),
                    null,
                    null,
                    columns.get(2).trim(),
                    emptyToNull(columns.get(3).trim()),
                    korisnik
            );
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Red " + lineNumber + " ima neispravan ukupanIznos",
                    e
            );
        }
    }

    private String toHeader(Row headerRow, DataFormatter formatter) {
        List<String> columns = new ArrayList<>();

        for (int cellIndex = 0; cellIndex < 7; cellIndex++) {
            columns.add(formatter.formatCellValue(headerRow.getCell(cellIndex)).trim());
        }

        return String.join(",", columns);
    }

    private boolean isBlankRow(Row row, DataFormatter formatter) {
        for (int cellIndex = 0; cellIndex < 7; cellIndex++) {
            if (!formatSpreadsheetCell(row.getCell(cellIndex), formatter, cellIndex).isBlank()) {
                return false;
            }
        }

        return true;
    }

    private String formatSpreadsheetCell(Cell cell, DataFormatter formatter, int cellIndex) {
        if (cell == null) {
            return "";
        }

        if (cellIndex == 5 && isNumericCell(cell)) {
            return BigDecimal.valueOf(cell.getNumericCellValue())
                    .setScale(0, RoundingMode.UNNECESSARY)
                    .toPlainString();
        }

        return formatter.formatCellValue(cell);
    }

    private boolean isNumericCell(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC
                || (cell.getCellType() == CellType.FORMULA
                && cell.getCachedFormulaResultType() == CellType.NUMERIC);
    }

    private List<String> splitCsvLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (character == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (character == ',' && !insideQuotes) {
                columns.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        columns.add(current.toString());
        return columns;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
