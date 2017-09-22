package demands;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by michal on 12.04.2017.
 */
@Value
public class DemandChanged {
    String productRefNo;
    List<Change> changes;

    @Value
    public static class Change {
        LocalDate day;
        long previous;
        long current;

        public long getDiff() {
            return current - previous;
        }
    }
}
