package lt.js.ratechanges.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Utils {

    @Bean
    public DecimalFormat decimalFormat() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
        df.setMinimumFractionDigits(4);
        df.setGroupingUsed(false);
        df.setParseBigDecimal(true);
        df.setDecimalFormatSymbols(dfs);
        return df;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
        return restTemplate;
    }
}
