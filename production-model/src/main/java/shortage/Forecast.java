package shortage;

import demands.DeliverySchema;
import lombok.AllArgsConstructor;
import stock.StockLevel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by michal on 02.02.2017.
 */
@AllArgsConstructor
public class Forecast {

    private final String productRefNo;
    private final SortedMap<LocalDate, DailyDemand> demandsPerDay;
    private final StockLevel stock;
    private final ProductionForecast productionForecast;

    public Optional<Shortages> findShortages(int daysAhead) {
        List<LocalDate> dates = Stream.iterate(LocalDate.now(), date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());
        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        long level = stock.getLevel();

        Shortages.Builder shortages = Shortages.builder(productRefNo, stock.getLocked());
        for (LocalDate day : dates) {
            DailyDemand dailyDemand = demandsPerDay.computeIfAbsent(day, DailyDemand::zero);
            long produced = productionForecast.outputFor(day);
            // TODO ASK should we add planned production output before delivery ?
            long levelOnDelivery = dailyDemand.levelOnDelivery(level, productionForecast);
            if (levelOnDelivery < 0) {
                shortages.add(day, levelOnDelivery);
            }
            long endOfDayLevel = level + produced - dailyDemand.level;
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return shortages.build();
    }

    @AllArgsConstructor
    public static class DailyDemand {

        private final LocalDate day;
        private final DeliverySchema deliverySchema;
        private final long level;

        public long getLevel() {
            return level;
        }

        public LocalDate getDay() {
            return day;
        }

        public static DailyDemand zero(LocalDate date) {
            return new DailyDemand(date, DeliverySchema.atDayStart, 0);
        }

        public long levelOnDelivery(long level, ProductionForecast productionForecast) {
            return deliverySchema.calculateLevelOnDelivery(level, this, productionForecast);
        }
    }
}
