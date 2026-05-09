package com.pdf.generator.controller;

import com.itextpdf.text.DocumentException;
import com.pdf.generator.entity.PdfDocument;
import com.pdf.generator.service.ServicePDF;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/pdf")
@CrossOrigin
@RequiredArgsConstructor
public class ControllerPDF {

	private static final DateTimeFormatter DATE_FORMATTER =
			DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private final ServicePDF servicePDF;

	@GetMapping("/export")
	public void exportPdf(HttpServletResponse response)
			throws IOException, DocumentException {
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		final String fileName = buildFileName();
		response.setHeader(
				HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=" + fileName
		);
		servicePDF.exportPDF(response);
	}

	@PostMapping("/generate")
	public ResponseEntity<PdfDocument> generatePdf(@RequestBody final PdfDocument PdfDocument) {
		final PdfDocument savedPdf = servicePDF.save(PdfDocument);
		return ResponseEntity.ok(savedPdf);
	}

	private String buildFileName() {
		final String date = LocalDateTime.now()
				.format(DATE_FORMATTER);
		return "pdf_" + date + ".pdf";
	}
}