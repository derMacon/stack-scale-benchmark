package dps.hoffmann.proxy.controller;

import dps.hoffmann.proxy.model.RequestType;
import dps.hoffmann.proxy.service.ScaleService;
import dps.hoffmann.proxy.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Controller handling the major rest api calls
 */
@RestController
@Slf4j
public class DefaultController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private ScaleService scaleService;


    /**
     * Liveness probe, can be used by metric scrapers like prometheus to
     * monitor service
     * @return true if component is up and running
     */
    @RequestMapping("/health")
    public boolean health() {
        log.info("component is healthy");
        return true;
    }

    /**
     * Endpoint that delegates the call to the actual scaler service with the
     * appropriate json body / endpoint args
     * @param jsonBody request body generated by the alert manager / prometheus
     */
    @PostMapping("/delegate")
    public void delegate(@RequestBody String jsonBody) {
        log.info("delegation endpoint called");
        RequestType type = translationService.translateRequest(jsonBody);
        log.info("translated request type from json body: {}", type);
        scaleService.scale(type);
    }




    // todo old stuff - delete this

    @RequestMapping("/receive")
    public void receiveGet(@RequestBody String jsonBody) {
        log.info("received get alert");
        callScaleApiOld("localhost:8080");
        callScaleApiOld("localhost:8743");
        callScaleApiOld("scaler:8080");
        callScaleApiOld("scaler:8743");
    }

    @PostMapping("/receive")
    public void receivePost(@RequestBody String jsonBody) {
        log.info("received post alert: {}", jsonBody);
        callScaleApiOld("localhost:8080");
        callScaleApiOld("localhost:8743");
        callScaleApiOld("scaler:8080");
        callScaleApiOld("scaler:8743");
    }

    private void callScaleApiOld(String host) {

        try {

            RestTemplate restTemplate = new RestTemplate();

//        String url = "http://localhost:8743/v1/scale-service";
//        String url = "http://scaler:8743/v1/scale-service";
            String url = "http://" + host + "/v1/scale-service";
            log.info("url: {}", url);
            String requestJson = "{" +
                    "\"groupLabels\": " +
                    "{\"scale\": \"up\", " +
                    "\"service\": \"vossibility_helloworld\"}" +
                    "}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
            String answer = restTemplate.postForObject(url, entity, String.class);
            System.out.println(answer);
            System.out.println("--------> success <--------");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}