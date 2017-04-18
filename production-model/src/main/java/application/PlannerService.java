package application;

import lombok.AllArgsConstructor;
import production.PlanRepository;
import production.ScheduleProduction;
import production.plan.AdjustProduction;
import production.plan.Line;
import production.plan.ProductionPlan;
import production.ProductionTime;

@AllArgsConstructor
public class PlannerService {

    private final PlanRepository plans;

    public void scheduleNewProduction(ScheduleProduction scheduleProduction) {
        ProductionPlan plan = plans.planForLine(scheduleProduction.getLineId());
        plan.scheduleNewProduction(scheduleProduction);

        // Production plan schedule new production on line, using form, at time for duration. 
        // >> productionPlan.scheduleNewProduction(line, form, time, duration) 
        // Only if form will be available at the time for whole duration, production may by planned. 
        // Production includes retooling time, parts output of used form and utilization of human resources 
        //     on that production stage. 
        // If production on given line overlaps with other: preceding must shrink,
        //                                                  succeeding one must start later. 
        // If consecutive productions use same from, retooling time between is zero.
        // Production forecasts (parts output * production duration) changes new and changed productions.
        // Overall utilization of human resources increases. 
        // Shortages may arise if insufficient production was planned.
    }

    public void adjustProductionTime(long productionId, ProductionTime time) {
        ProductionPlan plan = plans.planForProduction(productionId);
        plan.adjustProductionTime(new AdjustProduction(productionId, time));

        //Production plan adjust production to time and duration.
        // >> productionPlan.adjust(production, time, duration) 
        //If production on given line overlaps with other: preceding must shrink, 
        //succeeding one must start later. 
        //If consecutive productions use same from, retooling time between is zero. 
        //Production forecasts (parts output * production duration) changes for changed productions. 
        //Overall utilization of human resources may change. 
        //Shortages may arise if insufficient production was planned.
    }

    public void setColor(long productionId, String color) {
        // just sets color of production block on screen
        // CRUD
    }

    public void setNote(long productionId, String note) {
        // adds planner note for shift manager or production engineer
        // CRUD
    }

    public PlanView getPlan(Line line) {
        return null; // reed from db
    }
}
