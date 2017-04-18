package demands.callof;

import demands.DeliverySchema;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by michal on 11.04.2017.
 */
@Value
public class CallofDocument {

    LocalDate created;
    List<Product> products;

    @Value
    public static class Product {
        String productRefNo;
        LocalDate startDay;
        List<Daily> levels;
    }

    @Value
    public static class Daily {
        long level;
        DeliverySchema deliverySchema;
    }
}
