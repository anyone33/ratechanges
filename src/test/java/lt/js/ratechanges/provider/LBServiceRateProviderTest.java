package lt.js.ratechanges.provider;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lt.js.ratechanges.model.Rate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LBServiceRateProviderTest {

    @Autowired
    private LBServiceRateProvider provider;
    
    @Test
    public void testIllegalArgument() {
        try {
            provider.provideRatesForDate(null);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testRealInvocation() throws ParseException {
        Collection<Rate> rates = provider.provideRatesForDate(LocalDate.parse("2014-01-01"));
        assertNotNull(rates);
        assertTrue(rates.size() > 10);
    }


}
