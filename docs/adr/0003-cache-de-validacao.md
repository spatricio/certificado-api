# ADR 0003: cache de validacao no Redis

- Status: Aceita
- Decisao: o Gravador mantem no Redis respostas positivas da consulta por codigo de validacao, com TTL de cinco minutos.
- Contexto: consultas repetidas podem evitar leituras frequentes no banco do Gravador.
- Alternativas: sem cache; cache local por instancia; TTL sem invalidacao por evento.
- Motivo: Redis permite cache compartilhado entre instancias do Gravador. O TTL limita a idade dos dados; ao processar um evento para o codigo, o Gravador remove a entrada para evitar servir um resultado anterior.
- Limpeza: a entrada e removida no processamento de evento correspondente ou automaticamente apos cinco minutos. Respostas negativas nao sao armazenadas.
