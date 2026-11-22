# ADR 0003: cache de consultas no Redis

- Status: Aceita
- Decisao: o Gravador guarda no Redis respostas encontradas tanto pela consulta por ID do certificado quanto pelo codigo de validacao. O TTL de cinco minutos esta configurado em `gravador/src/main/resources/application.yml`.
- Contexto: consultas repetidas podem evitar leituras frequentes no PostgreSQL do Gravador; os certificados registrados sao imutaveis.
- Alternativas: nao usar cache; usar cache local por instancia; manter a copia indefinidamente.
- Motivo: Redis compartilha o cache entre instancias do Gravador. Cinco minutos limita o tempo de vida de cada copia sem exigir consistencia permanente, adequada a dados de certificado que nao mudam depois de emitidos.
- Nao encontrados: a consulta lanca `CertificateNotFoundException` antes de retornar um valor. Como nao ha resultado para armazenar, uma busca sem correspondencia nao e cacheada; uma proxima consulta volta ao banco e pode encontrar o registro se a mensagem tiver sido processada nesse intervalo.
- Limpeza: as entradas expiram automaticamente apos cinco minutos. Ao processar um evento, o Gravador tambem invalida as chaves correspondentes ao ID do certificado e ao codigo de validacao.
