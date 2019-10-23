package com.study.springframework.example05;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 10:20 PM
 */
public class Car {

    private int maxSpeed;
    private String brand;
    private double price;

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
