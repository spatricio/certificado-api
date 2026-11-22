# ADR 0004: perfil A com Docker Compose

- Status: Aceita
- Decisao: executar os dois servicos, dois PostgreSQL independentes, RabbitMQ e Redis com Docker Compose.
- Contexto: a entrega precisa ser demonstravel com dependencias reais e repetivel em uma maquina de desenvolvimento.
- Alternativas: perfil B com servicos externos ou em memoria; perfil C apenas conceitual.
- Motivo: o Compose descreve a topologia completa e deixa claras as dependencias e portas. Exige Docker Engine/Desktop em execucao.
