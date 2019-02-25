package lt.js.ratechanges.provider;

import java.time.LocalDate;
import java.util.Collection;

import lt.js.ratechanges.model.Rate;

public interface RateProvider {

    /**
     * Provides currency rates for specified date. 
     * 
     * @param forDate - date for which currency rates are provided, must not be <code>null</code>
     * @return - collection of {@link Rate} objects, never <code>null</code>
     */
    public Collection<Rate> provideRatesForDate(LocalDate forDate);

}
