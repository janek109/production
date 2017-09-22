package production;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Created by michal on 31.12.2016.
 */
@Value
@AllArgsConstructor(staticName = "of")
public class ProductionTime implements Comparable<ProductionTime> {

    public static final ProductionTime ETERNITY = new ProductionTime(LocalDateTime.MIN, Duration.ofSeconds(999999999L));

    private final LocalDateTime start;
    private final Duration duration;

    public LocalDateTime getEnd() {
        return start.plus(duration);
    }

    public ProductionTime startAfter(ProductionTime other) {
        return new ProductionTime(other.getEnd(), duration);
    }

    public ProductionTime stopBefore(ProductionTime other) {
        return new ProductionTime(start, Duration.between(start, other.start));
    }

    public boolean isBefore(ProductionTime other) {
        return start.isBefore(other.start) && this.getEnd().isBefore(other.start);
    }

    public boolean isAfter(ProductionTime other) {
        return other.isBefore(this);
    }

    public boolean overlapsWith(ProductionTime other) {
        return !this.isBefore(other) && !other.isBefore(this);
    }

    @Override
    public int compareTo(ProductionTime other) {
        return this.isBefore(other) ? -1 : other.isBefore(this) ? 1 : 0;
    }
}
