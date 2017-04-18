package shortage;

import demands.DemandChanged;
import lombok.AllArgsConstructor;
import production.plan.PlanChanged;
import production.Notifications;
import quality.QualityLock;
import quality.QualityTasks;
import stock.StockChanged;
import tools.Configuration;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by michal on 02.02.2017.
 */
@AllArgsConstructor
public class ShortagePredictionProcess {

    private String productRefNo;
    private Shortages shortages;

    private final ForecastProvider forecasts;
    private final Notifications notifications;
    private final QualityTasks qualityTasks;
    private final Configuration configuration;
    private final Clock clock;
    private final EventsContract events;

    public interface EventsContract {
        void emit(ShortageFound event);
    }

    public void onDemandChanged(DemandChanged demandChanged) {
        Forecast forecast = forecasts.forecastForProduct(demandChanged.getProductRefNo());
        LocalDate today = LocalDate.now(clock);
        Optional<Shortages> shortages = forecast.findShortages(
                configuration.shortagePredictionDaysAhead()
        );
        if (this.shortages.equals(shortages.orElse(null))) {
            return;
        }
        shortages.ifPresent(shortage -> {
            notifications.alertPlanner(shortage);
            if (shortage.getLockedParts() > 0 && shortage.anyBefore(
                    today.plusDays(configuration.increaseQATaskPriorityInDays()))) {
                qualityTasks.increasePriorityFor(demandChanged.getProductRefNo());
            }
            events.emit(new ShortageFound(shortage));
        });
        this.shortages = shortages.orElse(null);
    }

    public void onPlanChanged(PlanChanged planChanged) {
        LocalDate today = LocalDate.now(clock);

        Forecast forecast = forecasts.forecastForProduct(productRefNo);
        Optional<Shortages> shortages = forecast.findShortages(
                configuration.shortagePredictionDaysAhead()
        );
        shortages.ifPresent(shortage -> {
            notifications.markOnPlan(shortage);
            if (shortage.getLockedParts() > 0 && shortage.anyBefore(
                    today.plusDays(configuration.increaseQATaskPriorityInDays()))) {
                qualityTasks.increasePriorityFor(productRefNo);
            }
            events.emit(new ShortageFound(shortage));
        });
    }

    public void onStockChanged(StockChanged stockChanged) {
        Forecast forecast = forecasts.forecastForProduct(stockChanged.getProductRefNo());
        LocalDate today = LocalDate.now(clock);
        Optional<Shortages> shortages = forecast.findShortages(
                configuration.shortagePredictionDaysAhead()
        );
        shortages.ifPresent(shortage -> {
            notifications.alertPlanner(shortage);
            if (shortage.getLockedParts() > 0 && shortage.anyBefore(
                    today.plusDays(configuration.increaseQATaskPriorityInDays()))) {
                qualityTasks.increasePriorityFor(stockChanged.getProductRefNo());
            }
            events.emit(new ShortageFound(shortage));
        });
    }

    public void onLockedParts(QualityLock qualityLock) {
        Forecast forecast = forecasts.forecastForProduct(qualityLock.getProductRefNo());
        LocalDate today = LocalDate.now(clock);
        Optional<Shortages> shortages = forecast.findShortages(
                configuration.shortagePredictionDaysAhead()
        );
        shortages.ifPresent(shortage -> {
            notifications.softNotifyPlanner(shortage);
            if (shortage.getLockedParts() > 0 && shortage.anyBefore(
                    today.plusDays(configuration.increaseQATaskPriorityInDays()))) {
                qualityTasks.increasePriorityFor(qualityLock.getProductRefNo());
            }
            events.emit(new ShortageFound(shortage));
        });
    }

}
