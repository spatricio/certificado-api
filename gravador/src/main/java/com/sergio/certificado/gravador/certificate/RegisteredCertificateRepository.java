package com.sergio.certificado.gravador.certificate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegisteredCertificateRepository extends JpaRepository<RegisteredCertificate, UUID> {

	Optional<RegisteredCertificate> findByValidationCode(String validationCode);

	Optional<RegisteredCertificate> findByCertificateId(UUID certificateId);
}
