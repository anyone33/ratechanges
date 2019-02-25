package lt.js.ratechanges.service;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import lt.js.ratechanges.model.Change;
import lt.js.ratechanges.model.Rate;
import lt.js.ratechanges.provider.RateProvider;

@RunWith(SpringRunner.class)
public class ChangesServiceTest {

    @Mock
    private RateProvider mockProvider;
    
    @InjectMocks
    private ChangesService service;
    
    @Test
    public void testIllegalArgument() {
        try {
            service.getOrderedChanges(null);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testNoData() throws ParseException {
        doReturn(Collections.emptyList()).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-04")));
        Collection<Change> orderedChanges = service.getOrderedChanges(LocalDate.parse("2010-03-04"));
        assertThat(orderedChanges, Matchers.empty());
    }

    @Test
    public void testNoPreviousData() throws ParseException {
        List<Rate> currentRates = Arrays.asList(new Rate("AAA", new BigDecimal("10"), new BigDecimal("4.4444")));

        doReturn(currentRates).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-04")));
        doReturn(Collections.emptyList()).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-03")));
        
        Collection<Change> orderedChanges = service.getOrderedChanges(LocalDate.parse("2010-03-04"));
        assertThat(orderedChanges, contains(
                new Change("AAA", new BigDecimal("0.00000000"), "previous date rate not present")
                ));
    }

    @Test
    public void testCalculation() throws ParseException {
        List<Rate> currentRates = Arrays.asList(
                new Rate("AAA", new BigDecimal("10"), new BigDecimal("4.4444"))
                );
        List<Rate> previousRates = Arrays.asList(
                new Rate("AAA", new BigDecimal("100"), new BigDecimal("88.888"))
                );
        doReturn(currentRates).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-04")));
        doReturn(previousRates).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-03")));
        
        Collection<Change> orderedChanges = service.getOrderedChanges(LocalDate.parse("2010-03-04"));
        assertThat(orderedChanges, contains(
                new Change("AAA", new BigDecimal("0.50000000"), "")
                ));
    }

    @Test
    public void testOrdering() throws ParseException {
        List<Rate> currentRates = Arrays.asList(
                new Rate("AAA", new BigDecimal("10"), new BigDecimal("4.4444")),
                new Rate("BBB", new BigDecimal("10"), new BigDecimal("4.4444"))
                );
        List<Rate> previousRates = Arrays.asList(
                new Rate("AAA", new BigDecimal("10"), new BigDecimal("8.8888")),
                new Rate("BBB", new BigDecimal("10"), new BigDecimal("2.2222"))
                );
        doReturn(currentRates).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-04")));
        doReturn(previousRates).when(mockProvider).provideRatesForDate(eq(LocalDate.parse("2010-03-03")));
        
        Collection<Change> orderedChanges = service.getOrderedChanges(LocalDate.parse("2010-03-04"));
        assertThat(orderedChanges, contains(
                new Change("BBB", new BigDecimal("2.00000000"), ""),
                new Change("AAA", new BigDecimal("0.50000000"), "")
                ));
    }
}
