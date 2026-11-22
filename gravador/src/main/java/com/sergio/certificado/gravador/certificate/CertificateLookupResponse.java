package com.sergio.certificado.gravador.certificate;

import java.time.Instant;
import java.io.Serializable;
import java.util.UUID;

public record CertificateLookupResponse(
		UUID id,
		String codigoValidacao,
		String participante,
		String curso,
		Instant emitidoEm) implements Serializable {
}
