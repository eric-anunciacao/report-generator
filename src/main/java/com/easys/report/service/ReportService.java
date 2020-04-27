package com.easys.report.service;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Slf4j
@Service
public class ReportService {

	public static final String REPORT_PATH = "static/report/";

	public String gerarRelatorio(String id) {
		log.info("Gerando o relatório...");
		var parameters = new HashMap<String, Object>();
		parameters.put("P_VALOR", id);
		return gerarRelatorio("report.jasper", parameters);
	}

	private String gerarRelatorio(String relatorio, Map<String, Object> parametros) {
		return gerarRelatorio(relatorio, parametros, new ArrayList<Object>());
	}

	private String gerarRelatorio(String relatorio, Map<String, Object> parametros, List<Object> dados) {
		if (dados == null || dados.isEmpty()) {
			dados = new ArrayList<>();
			dados.add(new Object());
		}
		var dataSource = new JRBeanCollectionDataSource(dados);
		return gerarRelatorio(relatorio, parametros, dataSource);
	}

	private String gerarRelatorio(String relatorio, Map<String, Object> parametros, JRDataSource dataSource) {
		return gerarRelatorio(relatorio, parametros, dataSource, null);
	}

	private String gerarRelatorio(String relatorio, Map<String, Object> parametros, JRDataSource dataSource,
			Connection connection) {
		try {
			if (parametros == null) {
				parametros = new HashMap<>();
			}
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

			var path = this.getClass().getClassLoader().getResource("").getPath() + REPORT_PATH;
			var reportPath = path + relatorio;
			var inputStream = new FileInputStream(reportPath);
			var printer = getPrinter(parametros, dataSource, connection, inputStream);

			var exporter = new JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(printer));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path + "teste.pdf"));
			exporter.setConfiguration(reportConfig());
			exporter.setConfiguration(exportConfig());
			exporter.exportReport();

			log.info("Relatório gerado com sucesso!");
			return path + "teste.pdf";
		} catch (Exception e) {
			log.error("Erro ao imprimir o relatório. " + e);
			throw new RuntimeException(e);
		}
	}

	private JasperPrint getPrinter(Map<String, Object> parametros, JRDataSource dataSource, Connection connection,
			FileInputStream inputStream) throws JRException {
		if (connection != null) {
			return JasperFillManager.fillReport(inputStream, parametros, connection);
		} else {
			return JasperFillManager.fillReport(inputStream, parametros, dataSource);
		}
	}

	private SimplePdfExporterConfiguration exportConfig() {
		var exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("eric");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");
		return exportConfig;
	}

	private SimplePdfReportConfiguration reportConfig() {
		var reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);
		return reportConfig;
	}

}
