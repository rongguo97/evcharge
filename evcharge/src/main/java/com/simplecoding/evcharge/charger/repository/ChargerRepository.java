package com.simplecoding.evcharge.charger.repository;

import com.simplecoding.evcharge.charger.entity.Charger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargerRepository extends JpaRepository<Charger, Long> {
}
