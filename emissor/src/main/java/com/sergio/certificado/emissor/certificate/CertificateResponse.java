package com.sergio.certificado.emissor.certificate;

import java.time.Instant;
import java.util.UUID;

public record CertificateResponse(
		UUID id,
		String codigoValidacao,
		String participante,
		String curso,
		Instant emitidoEm) {
}
