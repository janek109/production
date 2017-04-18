import application.ExternalEvents;
import demands.Demand;
import demands.DemandChanged;
import shortage.ShortageFound;
import shortage.ShortagePredictionProcess;
import shortage.ShortagePredictionProcessRepository;
import production.plan.PlanChanged;
import production.plan.ProductionPlan;
import quality.QualityLock;
import stock.StockChanged;

import java.util.Set;

/**
 * Created by michal on 02.02.2017.
 */
public class EvetsMapping {

    ShortagePredictionProcessRepository processes;

    class Demands implements Demand.EventsContract {
        @Override
        public void emit(DemandChanged event) {
            processes.get(event.getProductRefNo())
                    .onDemandChanged(event);
        }
    }

    class Plan implements ProductionPlan.EventsContract {
        @Override
        public void emit(PlanChanged event) {
            Set<String> products = event.getProductRefNos();
            for (String productRefNo : products) {
                processes.get(productRefNo)
                        .onPlanChanged(event);
            }
        }
    }

    class Shortages implements ShortagePredictionProcess.EventsContract {
        @Override
        public void emit(ShortageFound event) {

        }
    }

    class External implements ExternalEvents {

        @Override
        public void emit(QualityLock event) {
            processes.get(event.getProductRefNo())
                    .onLockedParts(event);
        }

        @Override
        public void emit(StockChanged event) {
            processes.get(event.getProductRefNo())
                    .onStockChanged(event);
        }
    }
}
