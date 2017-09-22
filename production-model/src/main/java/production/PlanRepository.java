package production;

import production.plan.ProductionPlan;

/**
 * Created by michal on 01.02.2017.
 */
public interface PlanRepository {

    ProductionPlan planForLine(String line);

    ProductionPlan planForProduction(long productionId);
}
