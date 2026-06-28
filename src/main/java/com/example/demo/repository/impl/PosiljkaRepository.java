package com.example.demo.repository.impl;

import com.example.demo.entity.Posiljka;
import com.example.demo.entity.StatusPosiljke;
import com.example.demo.repository.IRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PosiljkaRepository implements IRepository<Posiljka,Long> {

    @PersistenceContext
    private EntityManager manager;

    @Override
    @Transactional
    public Posiljka save(Posiljka posiljka) {
        manager.persist(posiljka);
        return posiljka;
    }

    @Override
    @Transactional
    public List<Posiljka> saveAll(List<Posiljka> posiljke) {
        posiljke.forEach(manager::persist);
        return posiljke;
    }

    @Override
    public Optional<Posiljka> findById(Long id) {
        return Optional.ofNullable(manager.find(Posiljka.class, id));
    }

    @Override
    public List<Posiljka> findAll() {
        return manager.createNativeQuery("SELECT p.*\n" +
                        "FROM posiljka p\n" +
                        "JOIN (\n" +
                        "    SELECT serijski_broj, MAX(datum_izmene) AS poslednji_datum\n" +
                        "    FROM posiljka\n" +
                        "    GROUP BY serijski_broj\n" +
                        ") poslednja\n" +
                        "ON p.serijski_broj = poslednja.serijski_broj\n" +
                        "AND p.datum_izmene = poslednja.poslednji_datum", Posiljka.class)
                .getResultList();
    }


    @Override
    @Transactional
    public Posiljka update(Posiljka posiljka) {
        return saveNewVersion(posiljka);
    }

    public Optional<Posiljka> findBySerijskiBroj(String serijskiBroj) {
        return manager.createQuery(
                        "select p from Posiljka p where p.serijskiBroj = :serijskiBroj "
                                + "order by p.datumIzmene desc, p.id desc",
                        Posiljka.class
                )
                .setParameter("serijskiBroj", serijskiBroj)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<Posiljka> findLatestBySerijskiBroj(String serijskiBroj) {
        return findBySerijskiBroj(serijskiBroj);
    }


    public List<Posiljka> findHistoryBySerijskiBroj(String serijskiBroj) {
        return manager.createQuery(
                        "select p from Posiljka p where p.serijskiBroj = :serijskiBroj "
                                + "order by p.datumIzmene asc, p.id asc",
                        Posiljka.class
                )
                .setParameter("serijskiBroj", serijskiBroj)
                .getResultList();
    }

    public List<Posiljka> filterLatest(Long korisnikId, StatusPosiljke status,
                                       LocalDateTime kreiranoOd, LocalDateTime kreiranoDo) {
        StringBuilder query = new StringBuilder("select p from Posiljka p where 1 = 1");

        query.append(" and p.datumIzmene = (")
                .append("select max(poslednja.datumIzmene) from Posiljka poslednja ")
                .append("where poslednja.serijskiBroj = p.serijskiBroj")
                .append(")");

        if (korisnikId != null) {
            query.append(" and p.korisnik.id = :korisnikId");
        }

        if (status != null) {
            query.append(" and p.status = :status");
        }

        if (kreiranoOd != null) {
            query.append(" and exists (")
                    .append("select kreirana.id from Posiljka kreirana ")
                    .append("where kreirana.serijskiBroj = p.serijskiBroj ")
                    .append("and kreirana.status = :kreiranaStatus ")
                    .append("and kreirana.datumIzmene >= :kreiranoOd")
                    .append(")");
        }

        if (kreiranoDo != null) {
            query.append(" and exists (")
                    .append("select kreirana.id from Posiljka kreirana ")
                    .append("where kreirana.serijskiBroj = p.serijskiBroj ")
                    .append("and kreirana.status = :kreiranaStatus ")
                    .append("and kreirana.datumIzmene < :kreiranoDo")
                    .append(")");
        }

        query.append(" order by p.datumIzmene desc, p.id desc");

        TypedQuery<Posiljka> typedQuery = manager.createQuery(query.toString(), Posiljka.class);

        if (korisnikId != null) {
            typedQuery.setParameter("korisnikId", korisnikId);
        }

        if (status != null) {
            typedQuery.setParameter("status", status);
        }

        if (kreiranoOd != null || kreiranoDo != null) {
            typedQuery.setParameter("kreiranaStatus", StatusPosiljke.KREIRANA);
        }

        if (kreiranoOd != null) {
            typedQuery.setParameter("kreiranoOd", kreiranoOd);
        }

        if (kreiranoDo != null) {
            typedQuery.setParameter("kreiranoDo", kreiranoDo);
        }

        return typedQuery.getResultList();
    }

    @Transactional
    public Posiljka saveNewVersion(Posiljka posiljka) {
        Posiljka novaVerzija = new Posiljka();
        novaVerzija.setSerijskiBroj(posiljka.getSerijskiBroj());
        novaVerzija.setUkupanIznos(posiljka.getUkupanIznos());
        novaVerzija.setDatumIzmene();
        novaVerzija.setStatus(posiljka.getStatus());
        novaVerzija.setOpisSadrzaja(posiljka.getOpisSadrzaja());
        novaVerzija.setNapomenaIzmene(posiljka.getNapomenaIzmene());
        novaVerzija.setKorisnik(posiljka.getKorisnik());

        manager.persist(novaVerzija);
        return novaVerzija;
    }
}
