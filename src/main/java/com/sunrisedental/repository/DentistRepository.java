package com.sunrisedental.repository;

import com.sunrisedental.entity.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
}
