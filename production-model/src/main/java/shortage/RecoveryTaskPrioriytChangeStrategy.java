package shortage;

import java.time.LocalDate;

/**
 * Created by michal on 18.05.2017.
 */
public interface RecoveryTaskPrioriytChangeStrategy {

    RecoveryTaskPrioriytChangeStrategy onlyInXDaysAhead = (LocalDate today, Shortages shortage) ->
            shortage.getLockedParts() > 0 && shortage.anyBefore(
                    today.plusDays(2));

    RecoveryTaskPrioriytChangeStrategy never = (LocalDate today, Shortages shortage) -> false;

    boolean shouldIncreasePriority(LocalDate today, Shortages shortage);
}
