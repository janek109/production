package demands;


/**
 * Created by michal on 01.02.2017.
 */
public interface DemandRepository {
    Demand get(String productRefNo);

    void save(Demand aggregate);
}
