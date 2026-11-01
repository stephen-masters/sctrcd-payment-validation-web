package com.sctrcd.payments.repositories;

import com.sctrcd.payments.facts.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {}
