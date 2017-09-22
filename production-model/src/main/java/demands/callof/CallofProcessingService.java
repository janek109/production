package demands.callof;

import demands.Demand;
import demands.DemandRepository;
import lombok.AllArgsConstructor;

/**
 * Created by michal on 11.04.2017.
 */
@AllArgsConstructor
public class CallofProcessingService {

    private final DemandRepository repository;

    public void processDocument(CallofDocument document) {
        for (CallofDocument.Product product : document.getProducts()) {
            Demand demand = repository.get(product.getProductRefNo());
            demand.updateDemands(product);
            repository.save(demand);
        }
    }
}
