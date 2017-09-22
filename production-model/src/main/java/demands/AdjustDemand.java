package demands;

import lombok.Value;

import java.time.LocalDate;

/**
 * Created by michal on 01.02.2017.
 */
@Value
public class AdjustDemand {
    private final String productRefNo;
    private final LocalDate atDay;
    private final long level;
    private final DeliverySchema deliverySchema;
    private final String note;
}
