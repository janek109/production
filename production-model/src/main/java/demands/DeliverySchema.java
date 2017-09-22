package demands;

import shortage.Forecast;
import shortage.ProductionForecast;

/**
 * Created by michal on 03.02.2017.
 */
public interface DeliverySchema {

    DeliverySchema atDayStart = (level, dailyDemand, produced) -> level - dailyDemand.getLevel();

    DeliverySchema tillEndOfDay = (level, dailyDemand, productionForecast) ->
            level - dailyDemand.getLevel() + productionForecast.outputFor(dailyDemand.getDay());

    long calculateLevelOnDelivery(long level, Forecast.DailyDemand dailyDemand, ProductionForecast produced);
}
