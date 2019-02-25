package lt.js.ratechanges.provider;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lt.js.ratechanges.model.Rate;

@Component
public class LBServiceRateProvider implements RateProvider {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private LBServiceXmlParser xmlParser;

    @Value("${rate.provider.lb.url}")
    private String serviceUrl;
    
    @Override
    public Collection<Rate> provideRatesForDate(LocalDate forDate) {
        Assert.notNull(forDate, "forDate must be provided");
        
        Resource resource = retrieveRestResource(forDate);
        
        try (InputStream stream = resource.getInputStream()) {
            List<Rate> rates = xmlParser.parse(stream);
            return rates;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Resource retrieveRestResource(LocalDate forDate) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                .queryParam("Date", forDate.toString())
                .build();
        ResponseEntity<Resource> restExchange = restTemplate.exchange(uriComponents.encode().toUri(), HttpMethod.GET, new HttpEntity<>(buildHeaders()), Resource.class);
        return restExchange.getBody();
    }

    public static HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_XML));
        headers.set("User-Agent", "Mozilla/5.0 Firefox/26.0"); // LB service forbids access for unspecified User-Agent
        return headers;
    }
}
