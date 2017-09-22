package application;

import demands.AdjustDemand;
import demands.Demand;
import demands.DemandRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LogisticService {

    private final DemandRepository demandRepository;

    //Transactional
    public void adjustDemand(AdjustDemand adjustment) {
        Demand aggregate = demandRepository.get(adjustment.getProductRefNo());
        aggregate.adjustDemand(adjustment);
        demandRepository.save(aggregate);

        //Adjust demand at day to amount, delivered.
        // New demand is stored for further reference 
        //  We can change only Demands for today and future. 
        // Data from callof document should be preserved in database (DON’T OVERRIDE THEM).
        //  Should be possible to adjust demand even
        // if there was no callof document for that product.
        //   Logistician note should be kept along with adjustment.
        // If new demand is not fulfilled by  current product stock and production forecast 
        //   there is a shortage in particular days and we need to rise an alert. 
        //   planner should be notified, 
        //   if there are locked parts on stock, 
        //     QA task for recovering them should have high priority.
        //
    }

    public StockForecast getStockForecast(String productRefNo) {
        return null; // reed from db
    }
}
