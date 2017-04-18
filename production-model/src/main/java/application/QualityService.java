package application;

import lombok.AllArgsConstructor;
import quality.QualityLock;
import stock.StockLevel;
import stock.StockChanged;
import stock.StockRepository;

@AllArgsConstructor
public class QualityService {

    private final ExternalEvents events;
    private final StockRepository repository;

    public void lock(StorageUnit unit) {
        StockLevel stock = repository.getCurrentStock(unit.getProductRefNo());
        events.emit(new QualityLock(unit.getProductRefNo(), stock));

        // Lock all parts from storage unit on stock.
        //  >> stock.lock(storageUnit) 
        // parts from storage unit are locked on stock
        // If locking parts can lead to insufficient stock for next deliveries,
        //   parts recovery should have high priority. 
        // If there is a potential shortage in particular days,
        //   we need to rise an soft notification to planner.
    }

    public void unlock(StorageUnit unit, long recovered, long scrapped) {
        StockLevel stock = repository.getCurrentStock(unit.getProductRefNo());
        events.emit(new StockChanged(unit.getProductRefNo(), stock));

        // Unlock storage unit, recover X parts, Y parts was scrapped.
        // stock.unlock(storageUnit, recovered, scrapped) 
        // Recovered parts are back on stock. 
        // Scrapped parts are removed from stock. 
        // If demand is not fulfilled by current product stock and production forecast
        //   there is a shortage in particular days and we need to rise an alert.
    }
}
