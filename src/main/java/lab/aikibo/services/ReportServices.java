package lab.aikibo.services;

import org.apache.commons.io.IOUtils;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by tamami on 22/11/16.
 */
public class ReportServices {

    public ResponseEntity<byte[]> proses(String kdKecamatan) throws IOException, FileNotFoundException,
            ReportProcessingException {
        String filename = RefKelurahanReport.class.getSimpleName() + ".pdf";
        final File outputFilename = new File(filename);
        new RefKelurahanReport(kdKecamatan).generateReport(AbstractReportGenerator.OutputType.PDF, outputFilename);
        System.err.println("Generated the report [" + outputFilename.getAbsolutePath() + "]");

        byte[] contents = IOUtils.toByteArray(new FileInputStream(outputFilename));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));

        headers.add("content-disposition", "inline;filename=" + filename);

        //ga perlu ini kalo mau ditampilin di next tab, kalo pake ini artinya file langsung di unduh
        //headers.setContentDispositionFormData(filename, filename);

        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
        return response;
    }

}
