# ADR 0004: perfil A com Docker Compose

- Status: Aceita
- Decisao: executar os dois servicos, dois PostgreSQL independentes, RabbitMQ e Redis com Docker Compose.
- Contexto: a entrega precisa ser demonstravel com dependencias reais e repetivel em uma maquina de desenvolvimento.
- Alternativas: perfil B sem Docker, usando servicos instalados localmente ou em memoria; perfil C apenas conceitual, sem executar a topologia.
- Motivo: perfil B reduziria a exigencia de Docker, mas exigiria instalar e configurar varios servicos e nao garantiria o mesmo ambiente entre maquinas. Perfil C nao permitiria demonstrar o fluxo funcionando. O Compose descreve a topologia completa e inicializa dependencias reais com um comando, tornando a demonstracao repetivel.
- Consequencias: Docker Engine/Desktop precisa estar iniciado e as portas locais da aplicacao e dependencias precisam estar livres. Volumes preservam os dados entre execucoes; `docker compose down -v` remove esses dados explicitamente.
