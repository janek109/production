package production;

import production.plan.Form;
import production.plan.Line;

/**
 * Created by michal on 19.05.2017.
 */
public interface OpsSupport {
    void cantProduceWith(Form form, Line line);

}
