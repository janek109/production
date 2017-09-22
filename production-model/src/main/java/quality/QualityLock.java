package quality;

import lombok.Value;
import stock.StockLevel;

/**
 * Created by michal on 02.02.2017.
 */
@Value
public class QualityLock {
    String productRefNo;
    StockLevel stock;
}
