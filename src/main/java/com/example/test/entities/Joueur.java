package com.example.test.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Joueur implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String prenom;
    private double salaire;
    private int numero;
    private int matchs;
    private int buts;
    private String poste;
    private Equipe club;
    private Date dateNaissance;
    private boolean deleted; // checker si joueur est supprimé
    private boolean updated; // checker si joueur est modifié

    public Joueur(int id, String nom, String prenom, double salaire, int numero, int matchs, int buts, String poste,
                  Equipe club, Date dateNaissance) {
        super();
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.salaire = salaire;
        this.numero = numero;
        this.matchs = matchs;
        this.buts = buts;
        this.poste = poste;
        this.club = club;
        this.dateNaissance = dateNaissance;
    }

    public Joueur() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public int hashCode() {
        return Objects.hash(buts, club, dateNaissance, id, matchs, nom, numero, poste, prenom, salaire);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Joueur other = (Joueur) obj;
        return buts == other.buts && Objects.equals(club, other.club)
                && Objects.equals(dateNaissance, other.dateNaissance) && id == other.id && matchs == other.matchs
                && Objects.equals(nom, other.nom) && numero == other.numero && Objects.equals(poste, other.poste)
                && Objects.equals(prenom, other.prenom)
                && Double.doubleToLongBits(salaire) == Double.doubleToLongBits(other.salaire);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getMatchs() {
        return matchs;
    }

    public void setMatchs(int matchs) {
        this.matchs = matchs;
    }

    public int getButs() {
        return buts;
    }

    public void setButs(int buts) {
        this.buts = buts;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public void setEquipe(Equipe club) {
        this.club = club;
    }

    public Equipe getEquipe() {
        return club;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
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

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public String toString() {
        return "Joueur [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", salaire=" + salaire + ", numero="
                + numero + ", matchs=" + matchs + ", buts=" + buts + ", poste=" + poste + ", club=" + club.getNomEquipe() + ", dateNaissance="
                + dateFormat.format(dateNaissance);
    }


}
