package com.petboarding.Models;

public class Stay {

    private int stayId;
    private int petId;
    private String checkInDate;
    private String checkOutDate;
    private double dailyRate;
    private int grooming;
    private double totalCost;
    private String status;

    public Stay(int stayId, int petId, String checkInDate, String checkOutDate, double dailyRate, int grooming, double totalCost, String status) {
        this.stayId = stayId;
        this.petId = petId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.dailyRate = dailyRate;
        this.grooming = grooming;
        this.totalCost = totalCost;
        this.status = status;
    }

    public int getStayId() {
        return stayId;
    }
    public void setStayId(int stayId) {
        this.stayId = stayId;
    }
    public int getPetId() {
        return petId;
    }
    public void setPetId(int petId) {
        this.petId = petId;
    }
    public String getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }
    public String getCheckOutDate() {
        return checkOutDate;
    }
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    public double getDailyRate() {
        return dailyRate;
    }
    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }
    public int getGrooming() {
        return grooming;
    }
    public void setGrooming(int grooming) {
        this.grooming = grooming;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
