package com.example.test.entities;

import java.io.Serializable;
import java.util.Objects;

public class Equipe implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String nomEquipe;

	private boolean deleted; // checker si equipe est supprimé
	private boolean updated; // checker si equipe est modifié
	public Equipe(int id, String nomEquipe) {
		super();
		this.id = id;
		this.nomEquipe = nomEquipe;
	}
	public Equipe() {
		super();
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, nomEquipe);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Equipe other = (Equipe) obj;
		return id == other.id && Objects.equals(nomEquipe, other.nomEquipe);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNomEquipe() {
		return nomEquipe;
	}
	public void setNomEquipe(String nomEquipe) {
		this.nomEquipe = nomEquipe;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	@Override
	public String toString() {
		return "Equipe [id=" + id + ", nomEquipe=" + nomEquipe + "]";
	}

}
