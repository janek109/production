package demands;

/**
 * Created by michal on 03.02.2017.
 */
public class DefaultDeliverySchemaRules {

    public DeliverySchema defaultFor(String productRefNo) {
        if (productRefNo.startsWith("51")) {
            return DeliverySchema.tillEndOfDay;
        }
        return DeliverySchema.atDayStart;
    }
}
