package application;

import lombok.AllArgsConstructor;
import stock.StockLevel;
import stock.StockChanged;
import stock.StockRepository;

@AllArgsConstructor
public class WarehouseService {

    private final ExternalEvents events;
    private final StockRepository repository;

    public void registerNew(StorageUnit unit) {
        StockLevel stock = repository.getCurrentStock(unit.getProductRefNo());
        events.emit(new StockChanged(unit.getProductRefNo(), stock));

        //Register newly produced parts on stock. 
        // new parts are available on stock. 
        // If output from production is smaller than planned 
        //   it may lead to shortage in next days.
    }

    public void deliver(DeliveryNote note) {
        for (String productRefNo : note.getProducts()) {
            StockLevel stock = repository.getCurrentStock(productRefNo );
            events.emit(new StockChanged(productRefNo, stock));
        }

        //Remove delivered parts from stock.
        //  >> stock.delivered(deliveryNote)
        // If parts delivered during day exceed registered customer demand, 
        // demand for next day will be probably corrected with upcoming callof document,
        // but in rare cases it may be caused by not registered additional delivery 
        // (lack of manual adjustments of demand in system).
    }
}
