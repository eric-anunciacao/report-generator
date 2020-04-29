package com.easys.report.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easys.report.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportService service;

	@GetMapping("/{id}")
	public void gerarRelatorio(@PathVariable String id) {
		log.info("Gerando relatório " + id);
		service.gerarRelatorio(id);
	}

	@GetMapping("/pdf/{id}")
	public ResponseEntity<InputStreamResource> gerarRelatorioNaTela(@PathVariable String id) {
		try {
			log.info("Gerando relatório " + id);
			var headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=teste.pdf");
			var path = service.gerarRelatorio(id);
			var inputStream = new FileInputStream(path);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(inputStream));
		} catch (FileNotFoundException e) {
			return ResponseEntity.badRequest().build();
		}
	}

}
