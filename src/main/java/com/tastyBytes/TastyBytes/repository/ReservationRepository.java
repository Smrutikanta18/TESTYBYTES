package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation,Integer> {
}
