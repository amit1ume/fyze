package com.samartha.fyze.common.service;

import com.samartha.fyze.common.exception.GlobalExceptionHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
public class WebService {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*private final WebUtil webUtil;
    WebService(WebUtil webUtil){
        this.webUtil = webUtil;
    }*/


    public String getDailyBasisPrices(){
        try {
            StringBuilder url = new StringBuilder("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=INFY.BSE&apikey=FBCBHG9COMNQLW9Y");
         //   url.append(String.format(configUtils.getSocietyRatingsInfoEndpoint(), societyId));
          //  url.append("?user-id=").append(userId.toString());

           // String response = webUtil.doGet(url.toString(), null, 5000);
         //   return mapper.readValue(response, RatingsInfo.class);
       //     return response;
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url.toString()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            log.error("error while fetching ratings info from homes service for userId {}: {}","ass", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
