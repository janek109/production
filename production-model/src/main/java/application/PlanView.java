package application;

import lombok.Value;
import production.ProductionTime;

import java.time.Duration;
import java.util.List;

/**
 * Created by michal on 01.02.2017.
 */
@Value
public class PlanView {

    List<Production> productions;

    @Value
    public static class Production {
        long productionId;
        String productRefNo;
        String lineId;

        ProductionTime time;
        Duration startAndWormUp;
        Duration endAndCleaning;
        double speed;

        long output;
        double utilization;

        String color;
        String notes;
    }
}
