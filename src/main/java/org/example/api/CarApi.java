package org.example.api;

import org.example.core.annotations.Api;
import org.example.core.annotations.Body;
import org.example.core.annotations.Query;
import org.example.core.annotations.Request;
import org.example.core.enums.HttpMethod;
import org.example.model.Car;

import java.util.ArrayList;
import java.util.List;

@Api("/car")
public class CarApi {

    private final List<Car> carList = new ArrayList<>();

    @Request(method = HttpMethod.POST)
    public Car addCar(
            @Body Car car
    ) {
        carList.add(car);
        return car;
    }

    @Request(method = HttpMethod.GET)
    public List<Car> getCarList(
            @Query("brand") String brand
    ) {
        if (brand != null) {
            return carList.stream().filter(car -> car.brand().name().equals(brand)).toList();
        } else
            return carList;
    }

    @Request(method = HttpMethod.DELETE)
    public void clear() {
        carList.clear();
    }
}
