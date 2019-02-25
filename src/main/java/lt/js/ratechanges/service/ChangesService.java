package lt.js.ratechanges.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lt.js.ratechanges.model.Change;
import lt.js.ratechanges.model.Rate;
import lt.js.ratechanges.provider.RateProvider;

@Service
public class ChangesService {

    private final RateProvider rateProvider;
    
    public ChangesService(RateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }
    
    /**
     * Returns currency rate change information for specified date.<br>
     * Rate changes are returned in decreasing order.
     * 
     * @param forDate - date for currency change calculation, must not be <code>null</code>
     * @return - collection of {@link Change} objects, never <code>null</code>
     */
    public Collection<Change> getOrderedChanges(LocalDate forDate) {
        Assert.notNull(forDate, "forDate must be provided");
        
        Collection<Rate> currentRates = rateProvider.provideRatesForDate(forDate);
        if (CollectionUtils.isEmpty(currentRates)) {
            return Collections.emptyList();
        }
        Collection<Rate> previousRates = rateProvider.provideRatesForDate(forDate.minusDays(1));
        
        Map<String, Rate> currentRatesByCurrency = currentRates.stream().collect(Collectors.toMap(r -> r.getCurrency(), r -> r));
        Map<String, Rate> previousRatesByCurrency = previousRates.stream().collect(Collectors.toMap(r -> r.getCurrency(), r -> r));
        
        Set<Change> changes = new TreeSet<>(Comparator.comparing(Change::getChangeRatio).reversed().thenComparing(Change::getCurrency));
        for (String currency : currentRatesByCurrency.keySet()) {
            Change change = calculateChange(currency, currentRatesByCurrency.get(currency), previousRatesByCurrency.get(currency));
            changes.add(change);
        }
        
        return changes;
    }

    private Change calculateChange(String currency, Rate currentRate, Rate previousRate) {
        Assert.notNull(currency, "currency assumed to be not null");
        Assert.notNull(currentRate, "currentRate assumed to be not null");
        
        if (previousRate == null) {
            return new Change(currency, BigDecimal.valueOf(0, 8), "previous date rate not present");
        }
        
        BigDecimal changeRatio = currentRate.getRate().multiply(previousRate.getQuantity())
            .divide(previousRate.getRate().multiply(currentRate.getQuantity()), 8, RoundingMode.HALF_UP);
        return new Change(currency, changeRatio, "");
    }
    
}
