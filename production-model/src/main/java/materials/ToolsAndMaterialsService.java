package materials;

import production.ProductionTime;

/**
 * Created by michal on 18.04.2017.
 */
public interface ToolsAndMaterialsService {

    Availability tryAcquire(String productRefNo, long formId, ProductionTime time);

    void free(String productRefNo, long formId, ProductionTime time);
}
