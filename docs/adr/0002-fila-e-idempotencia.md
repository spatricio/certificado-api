# ADR 0002: RabbitMQ e processamento idempotente

- Status: Aceita
- Decisao: publicar eventos persistentes na fila duravel `certificado.emitido`; deduplicar no Gravador pela chave unica `eventId` gravada no banco dele.
- Contexto: o Emissor e o Gravador precisam trocar mensagens sem compartilhar banco, e uma mensagem pode ser entregue mais de uma vez.
- Alternativas: Kafka; chamada HTTP sincronica; fila em memoria; deduplicacao apenas em cache.
- Motivo: RabbitMQ e suficiente para uma fila de trabalho simples e funciona no perfil Docker. A restricao unica no PostgreSQL continua protegendo contra duplicatas mesmo depois que o cache expira ou o processo reinicia. O listener confirma a mensagem depois do processamento; uma falha deixa a mensagem elegivel para reentrega.
- Consequencia conhecida: gravar no banco do Emissor e publicar na fila nao formam uma transacao atomica. Uma etapa futura pode introduzir o padrao outbox e uma DLQ para aumentar a resiliencia operacional.
