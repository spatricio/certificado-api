package com.sergio.certificado.emissor.certificate;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificados")
public class CertificateController {

	private final CertificateService service;

	public CertificateController(CertificateService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<CertificateResponse> issue(@Valid @RequestBody CreateCertificateRequest request) {
		return ResponseEntity.accepted().body(service.issue(request));
	}
}
