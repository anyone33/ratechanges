package lt.js.ratechanges.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rate {

    private String currency;
    private BigDecimal quantity;
    private BigDecimal rate;

}
