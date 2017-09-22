package shortage;

import shortage.Shortages;

/**
 * Created by michal on 02.02.2017.
 */
public interface Notifications {
    void alertPlanner(Shortages shortage);

    void softNotifyPlanner(Shortages shortage);

    void markOnPlan(Shortages shortage);
}
