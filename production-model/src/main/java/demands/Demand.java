package demands;

import demands.callof.CallofDocument;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by michal on 01.02.2017.
 */
@AllArgsConstructor
public class Demand {
    private final String productRefNo;
    private final Map<LocalDate, DailyDemand> demands;
    private final Events events;
    private final Clock clock;

    public interface Events {
        void emit(DemandChanged event);
    }

    public void updateDemands(CallofDocument.Product product) {
        LocalDate today = LocalDate.now(clock);
        LocalDate day = product.getStartDay();

        List<DemandChanged.Change> changes = Collections.emptyList();
        for (CallofDocument.Daily daily : product.getLevels()) {
            // TODO ASK it may start in past, skip historic data, or persist?
            // update of past data is a bug or interesting variant?
            if (!day.isBefore(today)) {
                DailyDemand dailyDemand = demands.getOrDefault(day, zero());
                long previous = dailyDemand.getLevel();
                dailyDemand.original = new OriginalDemand(daily.getLevel(), daily.getDeliverySchema());
                // TODO ASK Is that correct or brake rules?
                long current = dailyDemand.getLevel();
                if (previous != current) {
                    changes.add(new DemandChanged.Change(day, previous, current));
                }
            }
            day = day.plusDays(1);
        }
        if (!changes.isEmpty()) {
            events.emit(new DemandChanged(
                    product.getProductRefNo(),
                    Collections.unmodifiableList(changes)
            ));
        }
    }

    public void adjustDemand(AdjustDemand newDemand) {
        if (newDemand.getAtDay().isBefore(LocalDate.now(clock))) {
            return; // TODO it is UI issue or reproduced post
        }
        long previous = getLevel(newDemand.getAtDay());
        DailyDemand dailyDemand = demands.getOrDefault(newDemand.getAtDay(), zero());
        dailyDemand.adjustment.add(new ManualAdjustment(
                newDemand.getLevel(),
                newDemand.getDeliverySchema(),
                newDemand.getNote()));
        long current = getLevel(newDemand.getAtDay());

        if (previous != current) {
            events.emit(new DemandChanged(
                    productRefNo,
                    Collections.singletonList(
                            new DemandChanged.Change(newDemand.getAtDay(), previous, current)
                    )
            ));
        }
    }

    private long getLevel(LocalDate date) {
        if (demands.containsKey(date)) {
            return demands.get(date).getLevel();
        }
        return 0;
    }

    private DailyDemand zero() {
        return new DailyDemand(new OriginalDemand(0, DeliverySchema.atDayStart));
    }

    private class DailyDemand {
        OriginalDemand original;
        List<ManualAdjustment> adjustment = new LinkedList<>();

        public DailyDemand(OriginalDemand original) {
            this.original = original;
        }

        long getLevel() {
            if (adjustment.isEmpty()) {
                return original.level;
            } else {
                return adjustment.get(adjustment.size() - 1).level;
            }
        }
    }

    @Value
    private static class OriginalDemand {
        long level;
        DeliverySchema deliverySchema;
    }

    @Value
    private class ManualAdjustment {
        long level;
        DeliverySchema deliverySchema;
        String note;
    }
}
