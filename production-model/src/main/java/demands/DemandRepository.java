package demands;

import demands.callof.CallofDocument;

import java.time.LocalDate;

/**
 * Created by michal on 01.02.2017.
 */
public interface DemandRepository {
    Demand get(String productRefNo);

    void saveOriginal(LocalDate created, LocalDate day, CallofDocument.Daily daily);
}
