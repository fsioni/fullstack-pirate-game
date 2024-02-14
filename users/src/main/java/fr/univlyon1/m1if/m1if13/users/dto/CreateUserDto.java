package fr.univlyon1.m1if.m1if13.users.dto;

import fr.univlyon1.m1if.m1if13.users.models.Species;

public class CreateUserDto {

    private String login;
    private Species species;
    private String password;

    private String image;

    public CreateUserDto() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}