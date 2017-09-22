package production;

import lombok.Value;
import production.ProductionTime;

@Value
public class AdjustProduction {
     long productionId;
     ProductionTime time;
}
