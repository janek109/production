package application;

import lombok.Value;

import java.util.List;

/**
 * Created by michal on 01.02.2017.
 */
@Value
public class DeliveryNote {
    private final List<String> products;
}
