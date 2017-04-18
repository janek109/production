package production.plan;

import lombok.AllArgsConstructor;
import lombok.Value;
import materials.Availability;
import materials.ToolsAndMaterialsService;
import production.ProductionTime;
import production.ScheduleProduction;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.toList;

/**
 * Created by michal on 31.12.2016.
 */
@AllArgsConstructor
public class ProductionPlan {

    private final Line line;
    private final Productions productions;
    private final ToolsAndMaterialsService toolsAndMaterials;
    private final EventsContract events;

    public interface EventsContract {
        void emit(PlanChanged event);
    }

    public void scheduleNewProduction(ScheduleProduction scheduleProduction) {
        Availability availability = toolsAndMaterials.tryAcquire(
                scheduleProduction.getProductRefNo(),
                scheduleProduction.getFormId(),
                scheduleProduction.getTime());
        if (!availability.isAvailable()) {
            // TODO Ask: what in that case ?
        }
        Form form = availability.getForm();
        if (line.canProduceWith(form)) {
            // TODO Ask: what in that case ?
        }
        Segment segment = productions.findAround(scheduleProduction.getTime());
        List<Production> changed = segment.makeSpace(this::rebookMaterials); // TODO Ask: what with materials availability issues ?
        Duration startDuration = segment.preceding.retoolingBefore(form);
        Duration cleanDuration = segment.next.get(0).retoolingAfter(form);

        Production newScheduled = new Production(line, form, scheduleProduction.getTime(), startDuration, cleanDuration, 1.0);
        productions.add(newScheduled);

        PlanChanged event = createEvent(changed, newScheduled);
        events.emit(event);
    }

    public void adjustProductionTime(AdjustProduction adjustProduction) {
        Production production = productions.get(adjustProduction.getProductionId());
        if (production.getTime().equals(adjustProduction.getTime())) {
            return;
        }
        Availability availability = rebookMaterials(production, adjustProduction.getTime());
        if (!availability.isAvailable()) {
            // TODO Ask: what in that case ?
        }

        Segment segment = productions.findAround(adjustProduction.getTime());
        List<Production> changed = segment.makeSpace(this::rebookMaterials); // TODO Ask: what with materials availability issues ?
        production.setTime(adjustProduction.getTime());
        PlanChanged event = createEvent(changed, null);
        events.emit(event);
    }

    private PlanChanged createEvent(List<Production> changed, Production newScheduled) {
        List<PlanChanged.Change> changes = changed.stream().map(prod -> new PlanChanged.Change(
                PlanChanged.Diff.CHANGED,
                prod.getProductionId(),
                prod.getForm().getProductRefNo(),
                prod.getTime()
        )).collect(toList());
        if (newScheduled != null) {
            changes.add(new PlanChanged.Change(
                    PlanChanged.Diff.NEW,
                    newScheduled.getProductionId(),
                    newScheduled.getForm().getProductRefNo(),
                    newScheduled.getTime()
            ));
        }
        return new PlanChanged(Collections.unmodifiableList(changes));
    }

    private Availability rebookMaterials(Production production, ProductionTime newTime) {
        toolsAndMaterials.free(
                production.getForm().getProductRefNo(),
                production.getForm().getFormId(),
                production.getTime());
        return toolsAndMaterials.tryAcquire(
                production.getForm().getProductRefNo(),
                production.getForm().getFormId(),
                newTime);
    }

    protected interface Productions {
        Production get(long id);

        void add(Production production);

        Segment findAround(ProductionTime time);
    }

    @Value
    protected static class Segment {

        private final ProductionTime aroundTime;

        /**
         * Preceding production or Idle pseudo production,
         * if nothing scheduled before.
         */
        private final Production preceding;

        /**
         * Overlapping and all later productions ordered by aroundTime,
         * at least single Idle pseudo production.
         */
        private final List<Production> next;

        public List<Production> makeSpace(BiConsumer<Production, ProductionTime> rebook) {
            List<Production> changed = new LinkedList<>();
            if (preceding.getTime().overlapsWith(aroundTime)) {
                // If production on given line overlaps with other: preceding must shrink
                ProductionTime newTime = preceding.getTime().stopBefore(aroundTime);
                rebook.accept(preceding, newTime);
                preceding.setTime(newTime);
                changed.add(preceding);
            }
            ProductionTime last = aroundTime;
            for (Production next : next) {
                if (next.getTime().overlapsWith(last)) {
                    // If production on given line overlaps with other: *ALL* succeeding one must start later.
                    ProductionTime newTime = next.getTime().startAfter(last);
                    rebook.accept(next, newTime);
                    next.setTime(newTime);
                    last = next.getTime();
                    changed.add(next);
                } else {
                    break;
                }
            }
            return changed;
        }
    }
}
