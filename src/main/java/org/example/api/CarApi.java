package api;

import core.annotations.API;
import core.annotations.GET;
import model.Brand;
import model.Car;

@API("/car")
public class CarApi {

    @GET
    public Car get() {
        return new Car("Model S", 2000.1, new Brand("Tesla", "USA"));
    }

}
