package com.pgms.pgmanage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pgms.pgmanage.repository.ManagerRepository;

@SpringBootApplication
public class PgManagementSystemApplication implements CommandLineRunner {

	@Autowired
	ManagerRepository managerRepository;

	@Override
	public void run(String... args) throws Exception {

	}

	public static void main(String[] args) {
		SpringApplication.run(PgManagementSystemApplication.class, args);
	}

}
