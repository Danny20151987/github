package com.study.springframework.example05;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 10:21 PM
 */
public class CarFactoryBean implements FactoryBean<Car> {

    private String carInfo;

    @Override
    public Car getObject() throws Exception {
        Car car = new Car();
        String [] infos = carInfo.split(",");
        car.setMaxSpeed(Integer.valueOf(infos[1]));
        car.setBrand(infos[0]);
        car.setPrice(Double.valueOf(infos[2]));
        return car;
    }

    @Override
    public Class<?> getObjectType() {
        return Car.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public String getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }
}
