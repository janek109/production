package shortage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Levels missing to satisfy customer demand of particular product.
 * <p>
 * Created by michal on 22.10.2015.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class Shortages {

    private final String refNo;
    private final long lockedParts;
    private final LocalDate found;
    private final SortedMap<LocalDate, Long> shortages;

    public static Shortages.Builder builder(String refNo) {
        return new Builder(refNo);
    }

    public boolean anyBefore(LocalDate date) {
        return getShortages().firstKey().isBefore(date);
    }

    public static class Builder {
        private String refNo;
        private LocalDate found;
        private long lockedParts;

        private SortedMap<LocalDate, Long> gaps = new TreeMap<>();

        public Builder(String refNo) {
            this.refNo = refNo;
        }

        public Builder add(LocalDate date, long level) {
            gaps.put(date, level);
            return this;
        }

        public Optional<Shortages> build() {
            if (gaps.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(new Shortages(refNo, 0L, found, Collections.unmodifiableSortedMap(gaps)));
            }
        }
    }
}
