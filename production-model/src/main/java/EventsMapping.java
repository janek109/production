import application.ExternalEvents;
import demands.Demand;
import demands.DemandChanged;
import shortage.ShortageFound;
import shortage.ShortagePredictionProcess;
import shortage.ShortagePredictionProcessRepository;
import production.PlanChanged;
import production.plan.ProductionPlan;
import quality.QualityLock;
import stock.StockChanged;

/**
 * Created by michal on 02.02.2017.
 */
public class EventsMapping {

    ShortagePredictionProcessRepository processes;

    class Demands implements Demand.Events {
        @Override
        public void emit(DemandChanged event) {
            processes.get(event.getProductRefNo())
                    .onDemandChanged(event);
        }
    }

    class Plan implements ProductionPlan.Events {
        @Override
        public void emit(PlanChanged event) {
            for (String productRefNo : event.getProductRefNos()) {
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
