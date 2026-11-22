package com.sergio.certificado.emissor.certificate;

import jakarta.validation.constraints.NotBlank;

public record CreateCertificateRequest(
		@NotBlank String participante,
		@NotBlank String curso) {
}
