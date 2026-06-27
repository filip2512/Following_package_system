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
        return manager.createQuery("select p from Posiljka p", Posiljka.class)
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

    public List<Posiljka> findLatestVersions() {
        return manager.createQuery(
                        "select p from Posiljka p where p.datumIzmene = "
                                + "(select max(p2.datumIzmene) from Posiljka p2 "
                                + "where p2.serijskiBroj = p.serijskiBroj) "
                                + "order by p.datumIzmene desc",
                        Posiljka.class
                )
                .getResultList();
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

    public Optional<Posiljka> findBySerijskiBrojAndStatus(String serijskiBroj, StatusPosiljke status) {
        return manager.createQuery(
                        "select p from Posiljka p where p.serijskiBroj = :serijskiBroj "
                                + "and p.status = :status order by p.datumIzmene desc, p.id desc",
                        Posiljka.class
                )
                .setParameter("serijskiBroj", serijskiBroj)
                .setParameter("status", status)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<Posiljka> findCreatedBySerijskiBroj(String serijskiBroj) {
        return findBySerijskiBrojAndStatus(serijskiBroj, StatusPosiljke.KREIRANA);
    }

    public List<Posiljka> findByKorisnikId(Long korisnikId) {
        return manager.createQuery(
                        "select p from Posiljka p where p.korisnik.id = :korisnikId",
                        Posiljka.class
                )
                .setParameter("korisnikId", korisnikId)
                .getResultList();
    }

    public List<Posiljka> findByStatus(StatusPosiljke status) {
        return manager.createQuery(
                        "select p from Posiljka p where p.status = :status",
                        Posiljka.class
                )
                .setParameter("status", status)
                .getResultList();
    }

    public List<Posiljka> findByKorisnikIdAndStatus(Long korisnikId, StatusPosiljke status) {
        return manager.createQuery(
                        "select p from Posiljka p where p.korisnik.id = :korisnikId and p.status = :status",
                        Posiljka.class
                )
                .setParameter("korisnikId", korisnikId)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Posiljka> findByDatumIzmeneBetween(LocalDateTime od, LocalDateTime doDatuma) {
        return manager.createQuery(
                        "select p from Posiljka p where p.datumIzmene between :od and :doDatuma",
                        Posiljka.class
                )
                .setParameter("od", od)
                .setParameter("doDatuma", doDatuma)
                .getResultList();
    }

    public List<Posiljka> findCreatedBetween(LocalDateTime od, LocalDateTime doDatuma) {
        return manager.createQuery(
                        "select p from Posiljka p where p.status = :status "
                                + "and p.datumIzmene between :od and :doDatuma "
                                + "order by p.datumIzmene desc",
                        Posiljka.class
                )
                .setParameter("status", StatusPosiljke.KREIRANA)
                .setParameter("od", od)
                .setParameter("doDatuma", doDatuma)
                .getResultList();
    }

    public List<Posiljka> filter(Long korisnikId, StatusPosiljke status) {
        String query = "select p from Posiljka p where "
                + "(:korisnikId is null or p.korisnik.id = :korisnikId) and "
                + "(:status is null or p.status = :status)";

        return manager.createQuery(query, Posiljka.class)
                .setParameter("korisnikId", korisnikId)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Posiljka> filter(Long korisnikId, StatusPosiljke status,
                                 LocalDateTime kreiranoOd, LocalDateTime kreiranoDo) {
        StringBuilder query = new StringBuilder("select p from Posiljka p where 1 = 1");

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
                    .append("and kreirana.datumIzmene <= :kreiranoDo")
                    .append(")");
        }

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
