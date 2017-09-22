package stock;

/**
 * Created by michal on 01.02.2017.
 */
public interface StockRepository {
    StockLevel getCurrentStock(String productRefNo);
}
