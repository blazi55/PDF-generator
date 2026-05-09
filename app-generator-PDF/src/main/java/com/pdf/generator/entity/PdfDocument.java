package com.pdf.generator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pdf")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "font_size_title", nullable = false)
	private Integer fontSizeTitle;

	@Column(name = "font_size_description", nullable = false)
	private Integer fontSizeDescription;
}