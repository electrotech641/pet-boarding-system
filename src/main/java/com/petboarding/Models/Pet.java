//Packages
package com.petboarding.Models;

public class Pet {
    private int id;
    private int ownerId;
    private String name;
    private String species;
    private int age;
    private String notes;

    public Pet(int id, int ownerId, String name, String species, int age, String notes) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.age = age;
        this.notes = notes;
    }

    public int getPetId() {
        return id;
    }
    public int getOwnerId() {
        return ownerId;
    }
    public String getName() {
        return name;
    }
    public String getSpecies() {
        return species;
    }
    public int getAge() {
        return age;
    }
    public String getNotes() {
        return notes;
    }

    /*
        Setters
     */

    public void setPetId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSpecies(String species) {
        this.species = species;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return name + " (" + species + ") Age: " + age + " (" + notes + ")";
    }
}
