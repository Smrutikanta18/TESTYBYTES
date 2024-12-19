package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<Data,Integer> {
}
