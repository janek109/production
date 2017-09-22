package production.plan;

import lombok.Data;
import production.ProductionTime;

import java.time.Duration;

/**
 * Created by michal on 31.12.2016.
 */
@Data
public class Production {

    private long productionId;
    private Line line;
    private Form form;
    private ProductionTime time;
    private Duration startAndWormUp;
    private Duration endAndCleaning;
    private double speed;

    private long output; // for view only
    private double utilization; // for view only

    public Production(Line line, Form form, ProductionTime time, Duration startAndWormUp, Duration endAndCleaning, double speed) {
        this.line = line;
        this.form = form;
        this.time = time;
        this.startAndWormUp = startAndWormUp;
        this.endAndCleaning = endAndCleaning;
        this.speed = speed;
        this.output = (long) (this.form.getOutputPerMinute() * this.speed * this.time.getDuration()
                .minus(this.startAndWormUp).minus(this.endAndCleaning).toMinutes());
        this.utilization = this.form.getUtilization() * this.speed;
    }

    public Duration retoolingBefore(Form other) {
        return form.getFormId() == other.getFormId() ? Duration.ofMinutes(0) : form.getStartAndWormUp();
    }

    public Duration retoolingAfter(Form other) {
        return form.getFormId() == other.getFormId() ? Duration.ofMinutes(0) : form.getEndAndCleaning();
    }

    public static Production idle(Line line) {
        return new Production(line, Form.NONE, ProductionTime.ETERNITY, Duration.ZERO, Duration.ZERO, 0.0);
    }

    public static Production idle(Line line, ProductionTime time) {
        return new Production(line, Form.NONE, time, Duration.ZERO, Duration.ZERO, 0.0);
    }
}
