package production;

import lombok.Value;

@Value
public class ScheduleProduction {
    String lineId;
    long formId;
    String productRefNo;
    ProductionTime time;
}
