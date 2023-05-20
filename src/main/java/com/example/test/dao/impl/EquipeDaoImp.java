package com.example.test.dao.impl;

import com.example.test.dao.EquipeDao;
import com.example.test.entities.Equipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EquipeDaoImp implements EquipeDao {
	
	//injection de dependances
	private Connection conn= DB.getConnection();


		@Override
		public void insert(Equipe equipe) {
			PreparedStatement ps = null;

			try {
				ps = conn.prepareStatement(
						"INSERT INTO equipe (nomEquipe) VALUES (?)",
						Statement.RETURN_GENERATED_KEYS);

				ps.setString(1, equipe.getNomEquipe());
				

				int rowsAffected = ps.executeUpdate();

				if (rowsAffected > 0) {
					ResultSet rs = ps.getGeneratedKeys();

					if (rs.next()) {
						int id = rs.getInt(1);

						equipe.setId(id);
					}

					DB.closeResultSet(rs);
				} else {
					System.out.println("Aucune ligne renvoyé");;
				}
			} catch (SQLException e) {
				System.err.println("problème d'insertion d'une equipe");
			} finally {
				DB.closeStatement(ps);
			}
		
	}

	@Override
	public void update(Equipe equipe) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("UPDATE equipe SET nomEquipe = ? WHERE Id = ?");

			ps.setString(1, equipe.getNomEquipe());
			ps.setInt(2, equipe.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("problème de mise à jour d'une equipe");;
		} finally {
			DB.closeStatement(ps);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("DELETE FROM equipe WHERE id = ?");
			
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("problème de suppression d'une equipe");;
		} finally {
			DB.closeStatement(ps);
		}
		
	}

	@Override
	public Equipe findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * FROM equipe WHERE id = ?");

			ps.setInt(1, id);

			rs = ps.executeQuery();

			if (rs.next()) {
				Equipe equipe = new Equipe();

				equipe.setId(rs.getInt("Id"));
				equipe.setNomEquipe(rs.getString("nomEquipe"));

				return equipe;
			}

			return null;
		} catch (SQLException e) {
			System.err.println("problème de requête pour trouver une equipe");;
		return null;
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

	@Override
	public List<Equipe> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * FROM equipe");
			rs = ps.executeQuery();

			List<Equipe> listEquipe = new ArrayList<>();

			while (rs.next()) {
				Equipe equipe = new Equipe();

				equipe.setId(rs.getInt("Id"));
				equipe.setNomEquipe(rs.getString("NomEquipe"));

				listEquipe.add(equipe);
			}

			return listEquipe;
		} catch (SQLException e) {
			System.err.println("problème de requête pour sélectionner une équipe");;
		return null;
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}

	}

	@Override
	public Equipe findByName(String equipeName) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * FROM equipe WHERE nomEquipe = ?");
			ps.setString(1, equipeName);
			rs = ps.executeQuery();

			if (rs.next()) {
				Equipe equipe = new Equipe();
				equipe.setId(rs.getInt("Id"));
				equipe.setNomEquipe(rs.getString("nomEquipe"));
				return equipe;
			}

			return null;
		} catch (SQLException e) {
			System.err.println("Problème de requête pour trouver une équipe par son nom");
			return null;
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

}
