# Roteiro para a banca (10 minutos)

Divida o tempo em **6 minutos de apresentacao e demonstracao** e **4 minutos para perguntas**. Deixe o Docker Desktop e o Postman prontos antes de iniciar o cronometro; use um certificado de teste ja emitido para evitar esperar a fila durante a banca.

## Apresentacao e demonstracao — 6 minutos

### 0:00-0:45 | Problema e objetivo

> "Este projeto emite certificados e permite validar se foram registrados. O objetivo foi separar a emissao do registro, usando dois servicos e dois bancos, e demonstrar comunicacao assincrona, idempotencia e cache."

### 0:45-1:45 | Arquitetura

Mostre o diagrama do README. Aponte Emissor, outbox/PostgreSQL do Emissor, RabbitMQ, Gravador/PostgreSQL do Gravador e Redis. Explique que o cliente pode consultar o Gravador pelo ID do certificado ou pelo codigo de validacao.

### 1:45-2:45 | Emissao e fila

No Postman, envie **Emitir certificado**. Explique que o Emissor grava certificado e evento pendente na mesma transacao; o publicador envia o evento ao RabbitMQ e aguarda confirmacao. O Gravador recebe a mensagem e grava no banco dele.

### 2:45-3:30 | Idempotencia

Explique a garantia de entrega pelo menos uma vez: pode haver reentrega. Mostre o `eventId` como chave unica no banco do Gravador e a consulta de deduplicacao descrita no README. Se houver tempo, abra o painel RabbitMQ e mostre a fila sem mensagens pendentes.

### 3:30-4:30 | Consulta e cache

No Postman, consulte o certificado por ID. Explique que apenas respostas encontradas sao cacheadas no Redis por cinco minutos; respostas `404` nao sao armazenadas. Uma segunda consulta pode ser atendida pelo cache, sem nova leitura do PostgreSQL.

### 4:30-5:20 | Decisoes arquiteturais

Resuma os ADRs:

- **ADR-001:** dois servicos e bancos separados para isolar a propriedade dos dados, apesar do custo operacional maior.
- **ADR-002:** RabbitMQ e outbox para desacoplar os servicos e permitir reentregas; Kafka foi considerado, mas retencao e reproducao de eventos nao eram necessidades deste fluxo.
- **ADR-003:** Redis com TTL de cinco minutos para acelerar leituras, sem guardar respostas negativas.

### 5:20-6:00 | Execucao e uso de IA

Mostre no README o comando `docker compose up --build -d` e explique que a aplicacao roda no **perfil A**. Sobre IA, seja transparente e adapte esta fala ao seu processo:

> "Usei IA como apoio para explorar o projeto, implementar e revisar partes do codigo, criar testes e melhorar a documentacao. Eu revisei as decisoes e validei o resultado rodando os testes e conferindo o fluxo com os servicos. A IA foi uma ferramenta de apoio; a responsabilidade pelas escolhas e pela apresentacao e minha."

## Perguntas — reserve 4 minutos

**Por que RabbitMQ e nao Kafka?**

O fluxo atual e uma fila de trabalho entre um produtor e um consumidor. RabbitMQ atende entrega, confirmacao e reentrega; nao precisamos manter um historico reproduzivel nem distribuir eventos para varios consumidores.

**E se a mesma mensagem chegar duas vezes?**

A garantia e pelo menos uma vez, entao duplicatas sao possiveis. O Gravador usa `eventId` unico e a restricao do banco impede uma segunda linha.

**Por que existe o outbox?**

Para gravar o certificado e a intencao de publicar o evento na mesma transacao. Se o envio falhar, o evento permanece pendente e pode ser tentado novamente.

**Por que usar cache se os certificados nao mudam?**

As respostas sao estaveis depois do registro. O TTL de cinco minutos limita a permanencia de cada copia; a chegada de um evento invalida as chaves correspondentes e resultados inexistentes nao sao cacheados.

**O que voce faria a seguir?**

Adicionar testes de integracao/contrato entre os servicos e, conforme necessidade operacional, retry com backoff e dead-letter queue.
