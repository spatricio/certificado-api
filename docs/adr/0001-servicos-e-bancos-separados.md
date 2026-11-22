# ADR 0001: servicos e bancos separados

- Status: Aceita
- Decisao: manter Emissor e Gravador como aplicacoes Spring Boot independentes, cada uma com seu proprio PostgreSQL.
- Contexto: o requisito pede dois servicos com bancos separados; cada servico deve ser dono dos seus dados.
- Alternativas: um monolito com dois schemas; um unico banco compartilhado; separar os servicos sem separar os bancos.
- Motivo: um monolito ou banco compartilhado seriam mais simples de operar, mas reduziriam o isolamento que o projeto precisa demonstrar. Dois servicos com bancos independentes deixam clara a propriedade dos dados e impedem que um servico dependa das tabelas internas do outro. O custo operacional extra e aceitavel para este objetivo didatico.
- Consequencias: a comunicacao entre os servicos precisa ocorrer por um contrato de mensagem, e a execucao local exige mais componentes. Cada servico pode evoluir seu armazenamento sem expor o schema ao outro.
