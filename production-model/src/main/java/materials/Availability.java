package materials;

import lombok.Value;
import production.plan.Form;

/**
 * Created by michal on 18.04.2017.
 */
@Value
public class Availability {
    boolean available;
    Form form;
}
