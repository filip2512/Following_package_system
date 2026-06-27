package com.example.demo.repository.impl;

import com.example.demo.entity.Korisnik;
import com.example.demo.repository.IRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class KorisnikRepository implements IRepository<Korisnik,Long> {

    @PersistenceContext
    private EntityManager manager;

    @Override
    @Transactional
    public Korisnik save(Korisnik korisnik) {
        manager.persist(korisnik);
        return korisnik;
    }

    @Override
    @Transactional
    public List<Korisnik> saveAll(List<Korisnik> korisnici) {
        korisnici.forEach(manager::persist);
        return korisnici;
    }

    @Override
    public Optional<Korisnik> findById(Long id) {
        return Optional.ofNullable(manager.find(Korisnik.class, id));
    }

    @Override
    public List<Korisnik> findAll() {
        return manager.createQuery("select k from Korisnik k", Korisnik.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Korisnik update(Korisnik korisnik) {
        return manager.merge(korisnik);
    }

    public Optional<Korisnik> findByJmbg(String jmbg) {
        return manager.createQuery("select k from Korisnik k where k.jmbg = :jmbg", Korisnik.class)
                .setParameter("jmbg", jmbg)
                .getResultStream()
                .findFirst();
    }
}
