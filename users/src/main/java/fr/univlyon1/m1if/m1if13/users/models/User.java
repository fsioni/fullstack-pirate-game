package fr.univlyon1.m1if.m1if13.users.models;

import fr.univlyon1.m1if.m1if13.users.exceptions.MismatchPasswordException;

public class User {
    private final String login;
    private Species species;
    private String password;
    // Permet d'invalider une connexion même si le token est toujours valide
    private boolean connected = false;
    // Nom du fichier image qui représentera l'utilisateur sur la carte
    private String image;

    public User(String login, Species species, String password) {
        this.login = login;
        this.species = species;
        this.password = password;
    }

    public User(String login, Species species, String password, String image) {
        this.login = login;
        this.species = species;
        this.password = password;
        this.image = image;
    }

    public String getLogin() {
        return login;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public void setPassword(String password) {
        System.out.println("New user Password: " + password);
        this.password = password;
        System.out.println("User password is set to: " + this.password);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void authenticate(String password) {
        System.out.println("Provided Password: " + password);
        System.out.println("Current User password: " + this.password);
        if (!password.equals(this.password)) {
            throw new MismatchPasswordException();
        }
        this.connected = true;
    }

    public void disconnect() {
        this.connected = false;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}