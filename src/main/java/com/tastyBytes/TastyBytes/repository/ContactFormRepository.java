package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.ContactForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactFormRepository extends JpaRepository<ContactForm,Integer> {
}
