package shortage;

import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.SortedMap;

/**
 * Created by michal on 18.01.2017.
 */
@AllArgsConstructor
public class ProductionForecast {
    private SortedMap<LocalDate, Long> outputs;

    public long outputFor(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }

    public long outputFor(LocalDateTime time) {
        return outputs.getOrDefault(time.toLocalDate(), 0L);
    }
}
