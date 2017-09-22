package application;

import quality.QualityLock;
import stock.StockChanged;

/**
 * Created by michal on 03.02.2017.
 */
public interface ExternalEvents {
    void emit(QualityLock event);
    void emit(StockChanged event);
}
