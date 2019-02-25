package lt.js.ratechanges.provider;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lt.js.ratechanges.model.Rate;

@Component
public class LBServiceXmlParser {

    private final DecimalFormat decimalFormat;

    public LBServiceXmlParser(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    public List<Rate> parse(InputStream stream) {
        Assert.notNull(stream, "stream must be provided");

        List<Rate> rates = new ArrayList<>();
        Rate rate = null;
        String text = null;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            while (reader.hasNext()) {
                int Event = reader.next();

                switch (Event) {
                case XMLStreamConstants.START_ELEMENT: {
                    if ("item".equals(reader.getLocalName())) {
                        rate = new Rate();
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS: {
                    text = reader.getText().trim();
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    switch (reader.getLocalName()) {
                    case "item": {
                        validateRate(rate);
                        rates.add(rate);
                        break;
                    }
                    case "currency": {
                        rate.setCurrency(text);
                        break;
                    }
                    case "quantity": {
                        rate.setQuantity(parseNumber(text));
                        break;
                    }
                    case "rate": {
                        rate.setRate(parseNumber(text));
                        break;
                    }
                    case "message": {
                        break;
                    }
                    case "error": {
                        throw new RateParserException(text);
                    }
                    }
                    break;
                }
                }
            }
        } catch (XMLStreamException e) {
            throw new RateParserException(e);
        }
        return rates;
    }

    private void validateRate(Rate rate) {
        if (StringUtils.isEmpty(rate.getCurrency())) {
            throw new RateParserException("Missing currency code for " + rate.toString());
        }
        if (rate.getQuantity() == null) {
            throw new RateParserException("Missing quantity for " + rate.toString());
        }
        if (rate.getQuantity().compareTo(new BigDecimal(0)) <= 0) {
            throw new RateParserException("Zero or negative quantity for " + rate.toString());
        }
        if (rate.getRate() == null) {
            throw new RateParserException("Missing rate for " + rate.toString());
        }
        if (rate.getRate().compareTo(new BigDecimal(0)) <= 0) {
            throw new RateParserException("Zero or negative rate for " + rate.toString());
        }
    }

    private BigDecimal parseNumber(String inputValueStr) {
        if (!inputValueStr.matches("^?\\d+(\\.\\d+)?$")) {
            throw new RateParserException("Not a valid number " + inputValueStr);
        }
        try {
            BigDecimal inputValue = (BigDecimal) decimalFormat.parse(inputValueStr);
            return inputValue;
        } catch (ParseException e) {
            throw new RateParserException(e);
        }
    }

    public static class RateParserException extends RuntimeException {
        private static final long serialVersionUID = -8007115004813586271L;

        public RateParserException(String message) {
            super(message);
        }

        public RateParserException(Throwable cause) {
            super(cause);
        }
    }

}
