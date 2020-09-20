package com.lambda.watermyplants.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "plants")
@JsonIgnoreProperties(value = {"user"})
public class Plant extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long plantid;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String species;

    private String image;

    @Column(nullable = false)
    private int frequency;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonIgnoreProperties(value = "plants", allowSetters = true)
    private User user;

    public Plant() {
    }


    public Plant(User user, String nickname, String species, String image, int frequency ) {
        this.nickname = nickname;
        this.species = species;
        this.image = image;
        this.frequency = frequency;
        this.user = user;
    }

    public long getPlantid() {
        return plantid;
    }

    public void setPlantid(long plantid) {
        this.plantid = plantid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
