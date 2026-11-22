package com.sergio.certificado.gravador.certificate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/validacoes")
public class CertificateLookupController {

	private final CertificateRegistryService service;

	public CertificateLookupController(CertificateRegistryService service) {
		this.service = service;
	}

	@GetMapping("/{codigo}")
	public CertificateLookupResponse find(@PathVariable String codigo) {
		try {
			return service.findByValidationCode(codigo);
		} catch (CertificateNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
		}
	}
}
