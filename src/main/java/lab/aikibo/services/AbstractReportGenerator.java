package lab.aikibo.services;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.*;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

import java.io.*;
import java.util.Map;

/**
 * Created by tamami on 22/11/16.
 */
public abstract class AbstractReportGenerator {

    public static enum OutputType {
        PDF, EXCEL, HTML
    }

    public AbstractReportGenerator() {
        ClassicEngineBoot.getInstance().start();
    }

    public abstract MasterReport getReportDefinition();

    public abstract DataFactory getDataFactory();

    public abstract Map<String, Object> getReportParameters();

    public void generateReport(final OutputType outputType, File outputFile) throws IllegalArgumentException,
            IOException, ReportProcessingException {
        if(outputFile == null) {
            throw new IllegalArgumentException("The output file was not specified");
        }

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            generateReport(outputType, outputStream);
        } finally {
            if(outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void generateReport(final OutputType outputType, OutputStream outputStream) throws IllegalArgumentException,
            ReportProcessingException {
        if(outputStream == null) {
            throw new IllegalArgumentException("The output stream was not specified");
        }

        final MasterReport report = getReportDefinition();
        final DataFactory dataFactory = getDataFactory();

        if(dataFactory != null) {
            report.setDataFactory(dataFactory);
        }

        final Map<String, Object> reportParameters = getReportParameters();
        if(null != reportParameters) {
            for(String key : reportParameters.keySet()) {
                report.getParameterValues().put(key, reportParameters.get(key));
            }
        }

        AbstractReportProcessor reportProcessor = null;
        try {
            switch(outputType) {
                case PDF : {
                    final PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(),
                            outputStream, report.getResourceManager());
                    reportProcessor = new PageableReportProcessor(report, outputProcessor);
                    break;
                }
                case EXCEL : {
                    final FlowExcelOutputProcessor target = new FlowExcelOutputProcessor(report.getConfiguration(),
                            outputStream, report.getResourceManager());
                    reportProcessor = new FlowReportProcessor(report, target);
                    break;
                }
                case HTML: {
                    final StreamRepository targetRepository = new StreamRepository(outputStream);
                    final ContentLocation targetRoot = targetRepository.getRoot();
                    final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor((report.getConfiguration()));
                    final HtmlPrinter printer = new AllItemsHtmlPrinter(report.getResourceManager());
                    printer.setContentWriter(targetRoot, new DefaultNameGenerator(targetRoot, "index", "html"));
                    printer.setDataWriter(null, null);
                    printer.setUrlRewriter(new FileSystemURLRewriter());
                    outputProcessor.setPrinter(printer);
                    reportProcessor = new StreamReportProcessor(report, outputProcessor);
                    break;
                }
            }
            reportProcessor.processReport();
        } finally {
            if(reportProcessor != null) {
                reportProcessor.close();
            }
        }
    }

}
