package com.example.test.dao.impl;

import com.example.test.dao.EquipeDao;
import com.example.test.dao.JoueurDao;
import com.example.test.entities.Equipe;
import com.example.test.entities.Joueur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JoueurDaoImp implements JoueurDao {
	// injection de dependances
	private Connection conn = DB.getConnection();
	EquipeDao equipedao = new EquipeDaoImp();


	@Override
	public void insert(Joueur joueur) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(
					"INSERT INTO joueur (nom, prenom, salaire, numero, matchs, buts, poste, club, dateNaissance) VALUES (?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			// ps.setString(1, joueur.getNomEquipe());
			ps.setString(1, joueur.getNom());
			ps.setString(2, joueur.getPrenom());
			ps.setDouble(3, joueur.getSalaire());
			ps.setInt(4, joueur.getNumero());
			ps.setInt(5, joueur.getMatchs());
			ps.setInt(6, joueur.getButs());
			ps.setString(7, joueur.getPoste());
			ps.setInt(8, joueur.getEquipe().getId());
			ps.setDate(9, new Date(joueur.getDateNaissance().getTime()));

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = ps.getGeneratedKeys();

				if (rs.next()) {
					int id = rs.getInt(1);

					joueur.setId(id);
				}

				DB.closeResultSet(rs);
			} else {
				System.out.println("Aucune ligne renvoyé");
				;
			}
		} catch (SQLException e) {
			System.err.println("problème d'insertion d'un joueur");
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void update(Joueur joueur) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("UPDATE joueur SET nom = ?, prenom = ?, salaire = ?, numero =?"
					+ ", matchs = ?, buts = ?, poste = ?, club =? WHERE Id = ?");

			ps.setString(1, joueur.getNom());
			ps.setString(2, joueur.getPrenom());
			ps.setDouble(3, joueur.getSalaire());
			ps.setInt(4, joueur.getNumero());
			ps.setInt(5, joueur.getMatchs());
			ps.setInt(6, joueur.getButs());
			ps.setString(7, joueur.getPoste());
			if (joueur.getEquipe() != null) {
				ps.setInt(8, joueur.getEquipe().getId());
			} else {
				ps.setNull(8, java.sql.Types.INTEGER);
			}
			ps.setInt(9, joueur.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("problème de mise à jour d'un joueur");
			;
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("DELETE FROM joueur WHERE id = ?");

			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("problème de suppression d'un joueur");
			;
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public Joueur findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * FROM joueur WHERE id = ?");

			ps.setInt(1, id);

			rs = ps.executeQuery();

			if (rs.next()) {
				Joueur joueur = new Joueur();

				joueur.setId(rs.getInt("Id"));
				joueur.setNom(rs.getString("nom"));
				joueur.setPrenom(rs.getString("prenom"));
				joueur.setSalaire(rs.getDouble("salaire"));
				joueur.setNumero(rs.getInt("numero"));
				joueur.setMatchs(rs.getInt("matchs"));
				joueur.setButs(rs.getInt("buts"));
				joueur.setPoste(rs.getString("poste"));
				Equipe equipe = new Equipe();
				equipe.setId(rs.getInt("club"));
				joueur.setEquipe(equipe);
				joueur.setDateNaissance(rs.getDate("dateNaissance"));

				return joueur;
			}

			return null;
		} catch (SQLException e) {
			System.err.println("problème de requête pour trouver un joueur");
			;
			return null;
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}


	@Override
	public List<Joueur> findAll() {
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        ps = conn.prepareStatement("SELECT * FROM joueur");
	        rs = ps.executeQuery();

	        List<Joueur> listJoueur = new ArrayList<>();

	        while (rs.next()) {
	            Joueur joueur = new Joueur();

	            joueur.setId(rs.getInt("Id"));
	            joueur.setNom(rs.getString("nom"));
	            joueur.setPrenom(rs.getString("prenom"));
	            joueur.setSalaire(rs.getDouble("salaire"));
	            joueur.setNumero(rs.getInt("numero"));
	            joueur.setMatchs(rs.getInt("matchs"));
	            joueur.setButs(rs.getInt("buts"));
	            joueur.setPoste(rs.getString("poste"));
	            
	            Equipe equipe ;
	            /*equipe.setId(rs.getInt("club"));
	            equipe.setNomEquipe(rs.getString("nomEquipe"));
	            joueur.setEquipe(equipe);*/
				equipe = equipedao.findById(rs.getInt("club")) ;
				joueur.setEquipe(equipe);
	            
	            joueur.setDateNaissance(rs.getDate("dateNaissance"));

	            listJoueur.add(joueur);
	        }

	        return listJoueur;
	    } catch (SQLException e) {
	        System.err.println("problème de requête pour trouver des joueurs");
	        return null;
	    } finally {
	        DB.closeResultSet(rs);
	        DB.closeStatement(ps);
	    }
	}


	@Override
	public List<Joueur> findByEquipe(Equipe equipe) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * FROM joueur WHERE club = ?");

			ps.setInt(1, equipe.getId());

			rs = ps.executeQuery();
			List<Joueur> listJoueur = new ArrayList<>();
			while(rs.next()) {
				Joueur joueur = new Joueur();

				joueur.setId(rs.getInt("Id"));
				joueur.setNom(rs.getString("nom"));
				joueur.setPrenom(rs.getString("prenom"));
				joueur.setSalaire(rs.getDouble("salaire"));
				joueur.setNumero(rs.getInt("numero"));
				joueur.setMatchs(rs.getInt("matchs"));
				joueur.setButs(rs.getInt("buts"));
				joueur.setPoste(rs.getString("poste"));
				joueur.setEquipe(equipe);
				joueur.setDateNaissance(rs.getDate("dateNaissance"));

				 listJoueur.add(joueur);
			}

			return listJoueur;
		} catch (SQLException e) {
			System.err.println("problème de requête pour trouver un joueur par equipe");
			return null;
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

}
