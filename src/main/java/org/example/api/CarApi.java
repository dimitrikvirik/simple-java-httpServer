package org.example.api;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.annotations.Api;
import org.example.core.annotations.Request;
import org.example.core.context.HttpServerContext;
import org.example.core.enumns.HttpMethod;
import org.example.model.Car;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api("/car")
public class CarApi {

    private final List<Car> carList = new ArrayList<>();

    @Request(method = HttpMethod.POST)
    public Car addCar(
            HttpExchange httpExchange
    ) {

        try {
            Car car = HttpServerContext.objectmapper.readValue(httpExchange.getRequestBody(), Car.class);
            carList.add(car);
            return car;
        } catch (IOException e) {
            return null;
        }
    }

    @Request(method = HttpMethod.GET)
    public List<Car> getCarList() {
        return carList;
    }

    @Request(method = HttpMethod.DELETE)
    public void clear(){
        carList.clear();
    }
}
