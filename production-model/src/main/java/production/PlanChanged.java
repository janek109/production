package production;

import lombok.Value;
import production.ProductionTime;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by michal on 02.02.2017.
 */
@Value
public class PlanChanged {

    public enum Diff {NEW, CHANGED}

    private final List<Change> changes;

    public Set<String> getProductRefNos() {
        return changes.stream()
                .map(Change::getProductRefNo)
                .collect(Collectors.toSet());
    }
    @Value
    public static class Change {
        private final Diff diff;
        private final long productionId;
        private final String productRefNo;
        private final ProductionTime newTime;
    }
}
