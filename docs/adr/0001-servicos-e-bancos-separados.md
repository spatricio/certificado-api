# ADR 0001: servicos e bancos separados

- Status: Aceita
- Decisao: manter Emissor e Gravador como aplicacoes Spring Boot independentes, cada uma com seu proprio PostgreSQL.
- Contexto: o requisito pede dois servicos com bancos separados; cada servico deve ser dono dos seus dados.
- Alternativas: um monolito com dois schemas; um unico banco compartilhado; separar os servicos sem separar os bancos.
- Motivo: bancos separados tornam explicito o limite de propriedade e impedem que um servico dependa das tabelas internas do outro. O custo operacional extra e adequado ao objetivo didatico do projeto.
