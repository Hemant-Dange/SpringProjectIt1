package com.pgms.pgmanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pgms.pgmanage.entity.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {

	Manager findByEmail(String email);

}
