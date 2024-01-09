package com.codigo.msregister.repository;

import com.codigo.msregister.entity.PersonsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonsRepository extends JpaRepository<PersonsEntity,Integer> {
}
