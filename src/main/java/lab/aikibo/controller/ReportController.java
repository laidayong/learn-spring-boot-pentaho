package lab.aikibo.controller;

import lab.aikibo.services.ReportServices;
import org.apache.log4j.Logger;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by tamami on 22/11/16.
 */
@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class ReportController {

    static final Logger logger = Logger.getLogger(ReportController.class);

    ReportServices reportServices;

    @RequestMapping("/")
    public String main() {
        return "Hi, gunakan url berikut untuk masing-masing fungsinya :<br>" +
                "> /getDaftarKelurahan/{kodeKecamatan}";
    }

    @RequestMapping(value="/getDaftarKelurahan/{kdKecamatan}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDaftarKelurahan(@PathVariable("kdKecamatan") String kdKecamatan)
            throws ReportProcessingException {
        reportServices = new ReportServices();

        try {
            logger.debug(kdKecamatan);
            return reportServices.proses(kdKecamatan);
        } catch(IOException ioe) {
            logger.debug(ioe.toString());

            return null;
        }
    }


    public static void main(String args[]) throws Exception {
        SpringApplication.run(ReportController.class, args);
    }

}
