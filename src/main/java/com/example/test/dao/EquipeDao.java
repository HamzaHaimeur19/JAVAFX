package com.example.test.dao;

import com.example.test.entities.Equipe;

import java.util.List;



public interface EquipeDao {
	void insert(Equipe equipe);

	void update(Equipe equipe);

	void deleteById(Integer id);

	Equipe findById(Integer id);

	List<Equipe> findAll();

	Equipe findByName(String equipeName);

}
