package com.pdf.generator.service;

import com.pdf.generator.entity.PdfDocument;
import com.pdf.generator.repository.RepositoryPDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicePDFTest {

	@Mock
	private RepositoryPDF repositoryPDF;

	@InjectMocks
	private ServicePDF servicePDF;

	@Test
	void shouldSavePdfDocument() {

		PdfDocument input = new PdfDocument();
		input.setTitle("Test");

		when(repositoryPDF.save(input)).thenReturn(input);

		PdfDocument result = servicePDF.save(input);

		assertEquals("Test", result.getTitle());

		verify(repositoryPDF).save(input);
	}

	@Test
	void shouldGeneratePDFWithoutDeletingWhenSingleRecord() throws Exception {

		PdfDocument pdf = buildPDF(1L);

		when(repositoryPDF.findAll()).thenReturn(List.of(pdf));

		MockHttpServletResponse response = new MockHttpServletResponse();

		servicePDF.exportPDF(response);

		assertNotNull(response.getContentAsByteArray());

		verify(repositoryPDF, never()).delete(any());
		verify(repositoryPDF, times(2)).findAll();
	}

	@Test
	void shouldDeleteOldestAndGeneratePDF() throws Exception {

		PdfDocument older = buildPDF(1L);
		PdfDocument newer = buildPDF(2L);

		when(repositoryPDF.findAll())
				.thenReturn(Arrays.asList(older, newer));

		MockHttpServletResponse response = new MockHttpServletResponse();

		servicePDF.exportPDF(response);

		verify(repositoryPDF).delete(older);

		assertNotNull(response.getContentAsByteArray());
	}

	@Test
	void shouldThrowWhenNoDataFound() {

		when(repositoryPDF.findAll())
				.thenReturn(Collections.emptyList());

		MockHttpServletResponse response = new MockHttpServletResponse();

		assertThrows(
				IllegalStateException.class,
				() -> servicePDF.exportPDF(response)
		);
	}

	private PdfDocument buildPDF(Long id) {

		PdfDocument pdf = new PdfDocument();

		pdf.setId(id);
		pdf.setTitle("Title " + id);
		pdf.setDescription("Description " + id);
		pdf.setFontSizeTitle(18);
		pdf.setFontSizeDescription(12);

		return pdf;
	}
}