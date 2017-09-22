package production.plan;

import lombok.Value;
import production.ProductionTime;

import java.time.Duration;

/**
 * Created by michal on 31.12.2016.
 */
@Value
public class Form {

    public static final Form NONE = new Form(-1, "<none>", 0.0, 0.0, Duration.ZERO, Duration.ZERO, 0);

    long formId;
    String productRefNo;
    double outputPerMinute;
    double utilization;
    Duration startAndWormUp;
    Duration endAndCleaning;
    int weight;
}
