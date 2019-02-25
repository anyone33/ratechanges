package lt.js.ratechanges.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Change {

    final private String currency;
    final private BigDecimal changeRatio;
    final private String message;

}
