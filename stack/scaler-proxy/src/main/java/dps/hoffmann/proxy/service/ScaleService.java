package dps.hoffmann.proxy.service;

import dps.hoffmann.proxy.model.ScalingInstruction;
import dps.hoffmann.proxy.properties.DelegationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static dps.hoffmann.proxy.model.ScalingDirection.DOWN;

/**
 * Service that makes the actual api calls to the scaler api service
 */
@Service
@Slf4j
public class ScaleService {

    private static final String API_ANSWER_SCALED_TO_MIN_REPL =
            ".*Scaling .* from .* to 1 replicas.*" +
                    "|.*is already descaled to the minimum number.*";

    private static final String urlFormat = "http://%s:%s%s";
    private String apiUrl;

    @Autowired
    private DelegationProperties delegationProperties;

    @Autowired
    private RestTemplate restTemplate;

    public ScaleService(DelegationProperties delegationProperties) {
        apiUrl = String.format(urlFormat,
                delegationProperties.getHostname(),
                delegationProperties.getPort(),
                delegationProperties.getEndpoint());
        log.info("api url: {}", apiUrl);
    }

    /**
     * Makes the call to the scaler service with the appropriate request body
     * @param instruction holds information about the type of the request,
     *                   will be translated to the correct request
     */
    public boolean sendScaleRequest(ScalingInstruction instruction) {
        String requestJson = delegationProperties.getRequestBody(instruction);
        log.info("request json: {}", requestJson);

        String answer = null;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            answer = restTemplate.postForObject(apiUrl, entity, String.class);
            log.info("scaler api answer: {}", answer);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return answer != null
                && answer.toLowerCase().matches(API_ANSWER_SCALED_TO_MIN_REPL);
    }

}