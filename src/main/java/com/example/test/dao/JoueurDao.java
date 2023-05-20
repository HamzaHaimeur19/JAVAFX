package com.example.test.dao;

import com.example.test.entities.Equipe;
import com.example.test.entities.Joueur;

import java.util.List;



public interface JoueurDao {
	void insert(Joueur joueur); // méthode pour ajouter nouveau joueur

	void update(Joueur joueur); // méthode pour mettre à jour un joueur

	void deleteById(Integer id); // méthode pour supprimer par id

	Joueur findById(Integer id); // méthode pour chercher joueur par id

	List<Joueur> findAll(); // méthode pour lister tous les equipes

	List<Joueur> findByEquipe(Equipe equipe); // méthode pour avoir joueur par équipe

}
