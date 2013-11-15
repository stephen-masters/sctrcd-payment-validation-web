package com.sctrcd.payments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sctrcd.payments.facts.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

}
