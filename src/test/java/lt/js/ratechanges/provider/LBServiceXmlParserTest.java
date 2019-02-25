package lt.js.ratechanges.provider;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import lt.js.ratechanges.model.Rate;
import lt.js.ratechanges.provider.LBServiceXmlParser.RateParserException;
import lt.js.ratechanges.utils.Utils;

public class LBServiceXmlParserTest {

    private LBServiceXmlParser parser = new LBServiceXmlParser(new Utils().decimalFormat());

    @Test
    public void testIllegalArgument() {
        try {
            parser.parse(null);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testInvalidStream() {
        try {
            parser.parse(new ByteArrayInputStream("aaaaa".getBytes()));
            fail();
        } catch (RateParserException e) {
        }
    }

    @Test
    public void testMessageXML() {
        String xml = "<message> Parameter value is invalid! No data found. </message>";
        List<Rate> list = parser.parse(new ByteArrayInputStream(xml.getBytes()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testErrorXML() {
        try {
            String xml = "<error> Critical error! </error>";
            parser.parse(new ByteArrayInputStream(xml.getBytes()));
            fail();
        } catch (RateParserException e) {
            assertTrue(e.getMessage().contains("Critical error!"));
        }
    }

    @Test
    public void testInvalidData() {
        try {
            // note invalid "quantity"
            String xml = "<ExchangeRates>" 
                    + "<item><date>2004.01.15</date><currency>AED</currency><quantity>10a</quantity><rate>7.3904</rate><unit>LTL per 10 currency units</unit></item>"
                    + "</ExchangeRates>";
            parser.parse(new ByteArrayInputStream(xml.getBytes()));
            fail();
        } catch (RateParserException e) {
            assertTrue(e.getMessage().contains("Not a valid number"));
        }

        try {
            // note invalid "rate"
            String xml = "<ExchangeRates>" 
                    + "<item><date>2004.01.15</date><currency>AED</currency><quantity>10</quantity><rate>-7.3904</rate><unit>LTL per 10 currency units</unit></item>"
                    + "</ExchangeRates>";
            parser.parse(new ByteArrayInputStream(xml.getBytes()));
            fail();
        } catch (RateParserException e) {
            assertTrue(e.getMessage().contains("Not a valid number"));
        }

        try {
            // note missing "currency"
            String xml = "<ExchangeRates>" 
                    + "<item><date>2004.01.15</date><quantity>10</quantity><rate>7.3904</rate><unit>LTL per 10 currency units</unit></item>"
                    + "</ExchangeRates>";
            parser.parse(new ByteArrayInputStream(xml.getBytes()));
            fail();
        } catch (RateParserException e) {
            assertTrue(e.getMessage().contains("Missing currency"));
        }

        try {
            // note missing "rate"
            String xml = "<ExchangeRates>" 
                    + "<item><date>2004.01.15</date><currency>AED</currency><quantity>10</quantity><unit>LTL per 10 currency units</unit></item>"
                    + "</ExchangeRates>";
            parser.parse(new ByteArrayInputStream(xml.getBytes()));
            fail();
        } catch (RateParserException e) {
            assertTrue(e.getMessage().contains("Missing rate"));
        }
    }

    @Test
    public void testValidData() {
        String xml = "<ExchangeRates>" 
                + "<item><date>2004.01.15</date><currency>AED</currency><quantity>10</quantity><rate>7.3904</rate><unit>LTL per 10 currency units</unit></item>"
                + "<item><date>2004.01.15</date><currency>ALL</currency><quantity>100</quantity><rate>2.5706</rate><unit>LTL per 100 currency units</unit></item>"
                + "</ExchangeRates>";
        List<Rate> rates = parser.parse(new ByteArrayInputStream(xml.getBytes()));
        
        assertThat(rates, contains(new Rate("AED", new BigDecimal("10"), new BigDecimal("7.3904")), new Rate("ALL", new BigDecimal("100"), new BigDecimal("2.5706"))));
    }

}
