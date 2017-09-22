package production.plan;

import lombok.Value;

/**
 * Created by michal on 31.12.2016.
 */
@Value
public class Line {
    int maxWeight;

    public boolean canProduceWith(Form form) {
        return maxWeight >= form.getWeight();
    }
}
