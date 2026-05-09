package com.pdf.generator.repository;

import com.pdf.generator.entity.PdfDocument;
import org.springframework.data.repository.CrudRepository;

public interface RepositoryPDF extends CrudRepository<PdfDocument, Long> {

}
