package com.sergio.certificado.gravador.certificate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/certificados")
public class CertificateByIdController {

	private final CertificateRegistryService service;

	public CertificateByIdController(CertificateRegistryService service) {
		this.service = service;
	}

	@GetMapping("/{id}")
	public CertificateLookupResponse findById(@PathVariable UUID id) {
		try {
			return service.findByCertificateId(id);
		} catch (CertificateNotFoundException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
		}
	}
}
