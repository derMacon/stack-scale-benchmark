package dps.hoffmann.proxy.controller;

import dps.hoffmann.proxy.model.ScalingInstruction;
import dps.hoffmann.proxy.service.PersistenceService;
import dps.hoffmann.proxy.service.RequestService;
import dps.hoffmann.proxy.service.ScaleService;
import dps.hoffmann.proxy.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling the major rest api calls
 */
@RestController
@Slf4j
public class DefaultController {

    @Autowired
    private RequestService requestService;

    @RequestMapping("/version")
    public String version() {
        String msg = "version: " + 2;
        log.info(msg);
        return msg;
    }


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
        log.info("called delegation endpoint");
        requestService.delegate(jsonBody);
    }

    @RequestMapping("/acknowledge")
    public void acknowledgeScale() {
        log.info("acknowledging scaled service");
        requestService.acknowledgeScaling();
    }

}
