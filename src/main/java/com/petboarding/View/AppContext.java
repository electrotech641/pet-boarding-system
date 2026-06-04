/*
 * This is where all the shared date is housed to keep from passing things around.
 * Keeps constructors clean and maintenance easy
 *
 */


//Package
package com.petboarding.View;

//Imports
import com.petboarding.Models.User;
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Repository.StayRepository;

import com.petboarding.Repository.UserRepository;
import com.petboarding.View.DataViews.PetTablePanel;
import com.petboarding.View.DataViews.OwnerTablePanel;
import com.petboarding.View.DataViews.CurrentStaysTablePanel;

import javax.swing.*;


public class AppContext {

    /*
     *   -------Shared Data---------
     *   -----+ Current User-------------
     */
    public final PetRepository petRepository;
    public final OwnerRepository ownerRepository;
    public final StayRepository stayRepository;
    public final UserRepository userRepository;
    public User currentUser;

    /*
     *  ----------------UI Panels-------------------
     */
    public PetTablePanel petTablePanel;
    public OwnerTablePanel ownerTablePanel;
    public CurrentStaysTablePanel currentStaysTablePanel;
    public JLabel statusLabel;

    //Constructor
    public AppContext(PetRepository petRepository, OwnerRepository ownerRepository, StayRepository stayRepository, UserRepository userRepository) {

        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
        this.stayRepository = stayRepository;
        this.userRepository = userRepository;
    }

    /*
     *  ----------------Refresh Methods
     */

    public void refreshPets() {
        if (petTablePanel != null) {
            petTablePanel.loadPetsIntoTable();
        }
    }

    public void refreshOwners() {
        if (ownerTablePanel != null) {
            ownerTablePanel.loadOwnersIntoTable();
        }
    }

    public void refreshCurrentStays() {
        if (currentStaysTablePanel != null) {
            currentStaysTablePanel.loadStaysIntoTable();
        }
    }

    // Refresh everything at once
    public void refreshAll() {
        refreshPets();
        refreshOwners();
        refreshCurrentStays();
    }
}
