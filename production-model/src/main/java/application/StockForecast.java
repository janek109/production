package application;

import lombok.Value;
import stock.StockLevel;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by michal on 01.02.2017.
 */
@Value
public class StockForecast {
    StockLevel stock;
    List<Forecast> forecast;

    @Value
    public static class Forecast {
        LocalDate date;
        long demand;
        long production;
        long level;
    }
}
