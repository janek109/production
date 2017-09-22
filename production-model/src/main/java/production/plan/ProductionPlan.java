package production.plan;

import lombok.AllArgsConstructor;
import lombok.Value;
import materials.ToolsAvailability;
import materials.ToolsAndMaterialsService;
import production.*;

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
    private final Events events;
    private final OpsSupport support;

    public interface Events {
        void emit(PlanChanged event);
    }

    public PlanChangeResult scheduleNewProduction(ScheduleProduction scheduleProduction) {
        ToolsAvailability availability = toolsAndMaterials.tryAcquire(
                scheduleProduction.getProductRefNo(),
                scheduleProduction.getFormId(),
                scheduleProduction.getTime());
        // Only if form will be available at the time for whole duration, production may by planned. 
        if (!availability.areToolsAvailable()) {
            // TODO Ask: what in that case ?
            // I assume, we should stop processing with feedback
            return PlanChangeResult.lackOfTools(availability);
        }
        Form form = availability.getForm();
        if (!line.canProduceWith(form)) {
            // TODO Ask: what in that case ? seams like UI error
            support.cantProduceWith(form, line);
        }
        Segment segment = productions.findAround(scheduleProduction.getTime());
        List<Production> changed = segment.makeSpace(this::rebookMaterials); // TODO Ask: what with materials availability issues ?
        Duration startDuration = segment.preceding.retoolingBefore(form);
        Duration cleanDuration = segment.next.get(0).retoolingAfter(form);

        Production newScheduled = new Production(line, form, scheduleProduction.getTime(), startDuration, cleanDuration, 1.0);
        productions.add(newScheduled);

        PlanChanged event = createEvent(changed, newScheduled);
        events.emit(event);
        return PlanChangeResult.planChanged(event);
    }

    public void adjustProductionTime(AdjustProduction adjustProduction) {
        Production production = productions.get(adjustProduction.getProductionId());
        if (production.getTime().equals(adjustProduction.getTime())) {
            return;
        }
        ToolsAvailability availability = rebookMaterials(production, adjustProduction.getTime());
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

    private ToolsAvailability rebookMaterials(Production production, ProductionTime newTime) {
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

    public static class PlanChangeResult {
        private final PlanChanged changes;
        private final ToolsAvailability problems;

        private PlanChangeResult(PlanChanged changes, ToolsAvailability problems) {
            this.changes = changes;
            this.problems = problems;
        }

        static PlanChangeResult lackOfTools(ToolsAvailability availability) {
            return new PlanChangeResult(null, availability);
        }

        static PlanChangeResult planChanged(PlanChanged event) {
            return new PlanChangeResult(event, null);
        }
    }
}
