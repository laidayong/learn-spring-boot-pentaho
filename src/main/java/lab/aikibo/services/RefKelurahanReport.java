package lab.aikibo.services;

import lombok.Data;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tamami on 22/11/16.
 */
@Data
public class RefKelurahanReport extends AbstractReportGenerator {

    private String kdKecamatan;

    public RefKelurahanReport() {}

    public RefKelurahanReport(String kdKecamatan) {
        this.kdKecamatan = kdKecamatan;
    }

    @Override
    public MasterReport getReportDefinition() {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            final URL reportDefinitionURL = classLoader.getResource("./refKelurahan.prpt");

            final ResourceManager resourceManager = new ResourceManager();
            final Resource directly = resourceManager.createDirectly(reportDefinitionURL, MasterReport.class);
            return (MasterReport) directly.getResource();
        } catch(ResourceException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DataFactory getDataFactory() {
        return null;
    }

    @Override
    public Map<String, Object> getReportParameters() {
        final Map parameters = new HashMap<String, Object>();
        parameters.put("kdKecamatan", getKdKecamatan());
        return parameters;
    }

}
