package shortage;

/**
 * Created by michal on 02.02.2017.
 */
public interface ForecastProvider {
    Forecast forecastForProduct(String productRefNo);
}
