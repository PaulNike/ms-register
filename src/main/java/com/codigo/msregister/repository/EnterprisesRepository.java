package com.codigo.msregister.repository;

import com.codigo.msregister.entity.EnterprisesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterprisesRepository extends JpaRepository<EnterprisesEntity,Integer> {
}
