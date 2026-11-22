package com.sergio.certificado.gravador.certificate;

public class CertificateNotFoundException extends RuntimeException {

	public CertificateNotFoundException() {
		super("Certificado nao encontrado.");
	}
}
