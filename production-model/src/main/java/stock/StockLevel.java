package stock;

import lombok.Value;

/**
 * Created by michal on 02.02.2017.
 */
@Value
public class StockLevel {
    long level;
    long locked;
}
