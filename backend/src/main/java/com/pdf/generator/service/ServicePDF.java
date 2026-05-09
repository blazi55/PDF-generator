package com.pdf.generator.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdf.generator.entity.PdfDocument;
import com.pdf.generator.repository.RepositoryPDF;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePDF {

	private final RepositoryPDF repositoryPDF;

	public PdfDocument save(final PdfDocument pdfDocument) {

		log.info("Saving PDF document with title: {}", pdfDocument.getTitle());

		PdfDocument savedDocument = repositoryPDF.save(pdfDocument);

		log.info("PDF document saved successfully with id: {}", savedDocument.getId());

		return savedDocument;
	}

	public void exportPDF(final HttpServletResponse response)
			throws IOException, DocumentException {

		log.info("Starting PDF export process");

		final List<PdfDocument> pdfList =
				(List<PdfDocument>) repositoryPDF.findAll();

		log.info("Found {} PDF documents in database", pdfList.size());

		if (pdfList.isEmpty()) {

			log.error("No PDF documents found");

			throw new IllegalStateException("No PDF data found");
		}

		removeOldRecords(pdfList);

		final PdfDocument latestPdf = getLatestPdf();

		log.info("Generating PDF for document id: {}", latestPdf.getId());

		generatePdf(response, latestPdf);

		log.info("PDF generated successfully");
	}

	private void removeOldRecords(final List<PdfDocument> pdfList) {

		if (pdfList.size() <= 1) {

			log.info("Only one PDF document found - skipping delete process");

			return;
		}

		final PdfDocument oldestPdf = pdfList.stream()
				.min(Comparator.comparingLong(PdfDocument::getId))
				.orElseThrow();

		log.info("Deleting oldest PDF document with id: {}", oldestPdf.getId());

		repositoryPDF.delete(oldestPdf);

		log.info("Oldest PDF document deleted successfully");
	}

	private PdfDocument getLatestPdf() {

		PdfDocument latestPdf = ((List<PdfDocument>) repositoryPDF.findAll())
				.stream()
				.max(Comparator.comparingLong(PdfDocument::getId))
				.orElseThrow();

		log.info("Latest PDF document found with id: {}", latestPdf.getId());

		return latestPdf;
	}

	private void generatePdf(
			final HttpServletResponse response,
			final PdfDocument pdfDocument
	) throws IOException, DocumentException {

		log.info("Creating PDF document structure");

		final Document document = new Document(PageSize.A4);

		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		final Font titleFont =
				createFont(pdfDocument.getFontSizeTitle());

		final Font descriptionFont =
				createFont(pdfDocument.getFontSizeDescription());

		final Paragraph title = createParagraph(
				pdfDocument.getTitle(),
				titleFont,
				Paragraph.ALIGN_CENTER
		);

		final Paragraph emptyLine = new Paragraph(" ");

		final Paragraph description = createParagraph(
				pdfDocument.getDescription(),
				descriptionFont,
				Paragraph.ALIGN_JUSTIFIED
		);

		document.add(title);
		document.add(emptyLine);
		document.add(description);

		document.close();

		log.info("PDF document closed successfully");
	}

	private Font createFont(final int size) {

		log.debug("Creating font with size: {}", size);

		final Font font =
				FontFactory.getFont(FontFactory.HELVETICA);

		font.setSize(size);

		return font;
	}

	private Paragraph createParagraph(
			final String text,
			final Font font,
			final int alignment
	) {

		log.debug("Creating paragraph with alignment: {}", alignment);

		final Paragraph paragraph = new Paragraph(text, font);

		paragraph.setAlignment(alignment);

		return paragraph;
	}
}