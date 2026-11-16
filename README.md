# Minha Refeição — Serviço de Assinatura de Marmitas (Implementação em Java)

Projeto desenvolvido para a disciplina de **Projeto de Software** — Tradução do Modelo Dinâmico (**Diagrama de Sequência UML**) e do **Diagrama de Classes de Projeto** em uma implementação prática em **Java**, com arquitetura **BCE (Boundary–Control–Entity)**, coleções e persistência em arquivos **CSV**.

---

## 👥 Integrantes do Grupo

| Nome | RA |
| --- | --- |
| **Eduardo Henrique de Souza Cruz** | `10358690` |
| **Vinicius Rampazi de Oliveira** | `10426499` |
| **Danilo Maciel de Freitas** | `10419452` |

---

## 🔗 Links da Documentação, Modelos na Wiki e Vídeo Explicativo

- 🎬 **[Vídeo Explicativo da Implementação em Java e Demonstração (YouTube)](https://youtu.be/xIyOvPayKi8)**
- ☕ **[Implementação em Java na Wiki (Entrega 4)](https://github.com/eduhsc7/projeto-de-software/wiki/Implementacao-em-Java)**
- 🏠 **[Página Inicial da Wiki](https://github.com/eduhsc7/projeto-de-software/wiki)**
- 📱 **[Protótipo — Storyboard e Diagrama de Telas (15 Telas)](https://github.com/eduhsc7/projeto-de-software/wiki/Prot%C3%B3tipo-%E2%80%90-Diagrama-de-Telas)**
- 📋 **[Lista de Classes Candidatas](https://github.com/eduhsc7/projeto-de-software/wiki/Lista-de-Classes-Candidatas)**
- 🧩 **[Diagrama de Classes de Domínio](https://github.com/eduhsc7/projeto-de-software/wiki/Diagrama-de-Classes-de-Dom%C3%ADnio)**
- 🔄 **[Cenário de Uso: Assinar Plano de Refeições (Diagrama de Sequência Completo + Classes de Projeto + Rastreabilidade)](https://github.com/eduhsc7/projeto-de-software/wiki/Cenario-Assinar-Plano-de-Refeicoes)**

---

## 🎯 Objetivo da Implementação

Esta implementação em Java realiza integralmente o cenário de uso **“Assinar Plano de Refeições”** da plataforma **Minha Refeição**, garantindo correspondência **1 para 1** com:
1. O **[Diagrama de Sequência Completo (`10-sequencia-completa.png`)](https://raw.githubusercontent.com/wiki/eduhsc7/projeto-de-software/assets/diagramas-sequencia/10-sequencia-completa.png)**, contemplando o **Fluxo Principal** (Telas 01 a 15) e os três **Fluxos Alternativos integrados (`FA1`, `FA2` e `FA3`)**;
2. O **[Diagrama de Classes de Projeto (`diagrama-classes-projeto.png`)](https://raw.githubusercontent.com/wiki/eduhsc7/projeto-de-software/assets/diagramas-projeto/diagrama-classes-projeto.png)**, preservando os mesmos pacotes/estereótipos (`«boundary»`, `«control»`, `«collection»`, `«processor»`, `«external»`, `«entity»`, `«datatype»` e `«enumeration»`), nomes de classes, atributos, visibilidades, assinaturas de métodos e responsabilidades de criação.

---

## 🏛️ Estrutura do Projeto e Correspondência com os Modelos UML

```text
projeto-de-software/
├── .vscode/                         # Configurações para execução direta no terminal do VS Code
│   ├── launch.json
│   ├── settings.json
│   └── tasks.json
├── dados/                           # Persistência automática em arquivos CSV (UTF-8)
│   ├── assinantes.csv
│   ├── codigos_confirmacao.csv
│   ├── planos.csv
│   ├── preferencias_alimentares.csv
│   ├── cardapios.csv
│   ├── itens_cardapio.csv
│   ├── enderecos_entrega.csv
│   ├── assinaturas.csv
│   ├── pedidos.csv
│   ├── itens_pedido.csv
│   ├── entregas.csv
│   └── pagamentos.csv
├── src/
│   ├── Main.java                    # Ponto de entrada (Menu Interativo + Demonstração Guiada + Leitor CSV)
│   ├── boundary/                    # Objetos de Interface / Fronteira («boundary»)
│   │   ├── TelaIdentificacao.java   # Telas 01 a 03 — Celular, SMS e Validação (com FA1)
│   │   ├── TelaContratacao.java     # Telas 04 a 12 — Planos, Preferências, Cardápio, Resumo (FA2) e Endereço
│   │   └── TelaPagamento.java       # Telas 13 a 15 — Pagamento, Autorização (FA3) e Confirmação
│   ├── controller/                  # Objeto Controlador («control»)
│   │   └── AssinaturaController.java
│   ├── collection/                  # Coleções e Persistência CSV («collection»)
│   │   ├── Assinantes.java
│   │   ├── Assinaturas.java
│   │   ├── Planos.java
│   │   ├── PreferenciasAlimentares.java
│   │   ├── Cardapios.java
│   │   └── EnderecosEntrega.java
│   ├── service/                     # Processadores e Integração Externa («processor» / «external»)
│   │   ├── ProcessadorSMS.java
│   │   ├── ProcessadorProtocolo.java
│   │   └── OperadoraCartao.java
│   └── entity/                      # Entidades de Domínio («entity»), Tipos de Dados e Enumerações
│       ├── Assinante.java
│       ├── CodigoConfirmacao.java
│       ├── PlanoAssinatura.java
│       ├── PreferenciaAlimentar.java
│       ├── Assinatura.java
│       ├── Pedido.java
│       ├── ItemPedido.java
│       ├── Cardapio.java
│       ├── ItemCardapio.java
│       ├── OpcaoCardapio.java
│       ├── EnderecoEntrega.java
│       ├── Entrega.java
│       ├── Pagamento.java
│       ├── DadosEndereco.java
│       ├── DadosCartao.java
│       ├── ConfirmacaoContratacao.java
│       ├── SituacaoCodigo.java
│       ├── SituacaoAssinatura.java
│       ├── SituacaoPedido.java
│       ├── SituacaoPagamento.java
│       └── CategoriaAlimento.java
├── executar.bat                     # Script para compilação e execução rápida no Windows (CMD/PowerShell)
└── executar.ps1                     # Script PowerShell para compilação e execução
```

---

## 🔍 Matriz de Rastreabilidade (Diagrama de Sequência ↔ Código Java)

### 1. Fronteira (`package boundary`)
| Classe Java | Estereótipo UML | Telas do Protótipo | Fluxos Realizados |
| --- | --- | --- | --- |
| `TelaIdentificacao` | `«boundary»` | Telas 01 a 03 | Identificação pelo celular, validação de código SMS e **FA1** (tentativas inválidas, bloqueio na 3ª falha e reenvio com substituição de código pendente). |
| `TelaContratacao` | `«boundary»` | Telas 04 a 12 | Consulta/confirmação de plano, preferências alimentares, seleção de pratos/acompanhamentos/sobremesas, validação e ajuste do pedido (**FA2**), cadastro de endereço e transição para `AGUARDANDO_PAGAMENTO`. |
| `TelaPagamento` | `«boundary»` | Telas 13 a 15 | Exibição do valor contratado, solicitação de pagamento à operadora, tratamento de recusa (**FA3** — nova tentativa, outro cartão ou encerramento) e exibição da `ConfirmacaoContratacao`. |

### 2. Controle (`package controller`)
| Classe | Operação implementada em Java | Fluxo no Diagrama de Sequência |
| --- | --- | --- |
| `AssinaturaController` | `iniciarContratacao(String celular): Assinante` | Principal |
| `AssinaturaController` | `confirmarCelular(Assinante assinante, String valorInformado): SituacaoCodigo` | Principal, FA1 |
| `AssinaturaController` | `reenviarCodigo(Assinante assinante): void` | FA1 |
| `AssinaturaController` | `consultarPlanos(): List<PlanoAssinatura>` | Principal |
| `AssinaturaController` | `contratarPlano(Assinante assinante, PlanoAssinatura plano): Assinatura` | Principal |
| `AssinaturaController` | `consultarPreferencias(): List<PreferenciaAlimentar>` | Principal |
| `AssinaturaController` | `definirPreferencias(Assinante assinante, List<PreferenciaAlimentar> preferencias): void` | Principal |
| `AssinaturaController` | `consultarRefeicoes(Assinatura assinatura): List<ItemCardapio>` | Principal |
| `AssinaturaController` | `montarPedido(Assinatura assinatura, Map<ItemCardapio, Integer> selecao): boolean` | Principal, FA2 |
| `AssinaturaController` | `consultarResumoPedido(Assinatura assinatura): List<ItemPedido>` | Principal, FA2 |
| `AssinaturaController` | `confirmarPedido(Assinatura assinatura): boolean` | Principal, FA2 |
| `AssinaturaController` | `definirLocalEntrega(Assinatura assinatura, DadosEndereco dados): LocalDate` | Principal |
| `AssinaturaController` | `obterValorPagamento(Assinatura assinatura): BigDecimal` | Principal |
| `AssinaturaController` | `pagarAssinatura(Assinatura assinatura, DadosCartao cartao): SituacaoPagamento` | Principal, FA3 |
| `AssinaturaController` | `consultarConfirmacao(Assinatura assinatura): ConfirmacaoContratacao` | Principal, FA3 |

### 3. Coleções (`package collection`) e Processadores/Integração (`package service`)
| Classe | Estereótipo | Operações conforme Diagrama de Sequência | Arquivo CSV Persistido |
| --- | --- | --- | --- |
| `Assinantes` | `«collection»` | `localizarOuCadastrar(celular)`, `-buscarPorCelular(celular)` | `dados/assinantes.csv`, `dados/codigos_confirmacao.csv` |
| `Assinaturas` | `«collection»` | `criar(assinante, plano)`, `guardar(assinatura)` | `dados/assinaturas.csv`, `dados/pedidos.csv`, `dados/itens_pedido.csv`, `dados/entregas.csv`, `dados/pagamentos.csv` |
| `Planos` | `«collection»` | `listar()` | `dados/planos.csv` |
| `PreferenciasAlimentares` | `«collection»` | `listar()` | `dados/preferencias_alimentares.csv` |
| `Cardapios` | `«collection»` | `buscarVigente(data)` | `dados/cardapios.csv`, `dados/itens_cardapio.csv` |
| `EnderecosEntrega` | `«collection»` | `cadastrar(dados)` | `dados/enderecos_entrega.csv` |
| `ProcessadorSMS` | `«processor»` | `enviar(celular, mensagem)` | Simulação no terminal |
| `ProcessadorProtocolo` | `«processor»` | `gerar()` | Protocolo gravado em `dados/assinaturas.csv` |
| `OperadoraCartao` | `«external»` | `autorizar(valor, cartao)` | Resultado gravado em `dados/pagamentos.csv` |

---

## ▶️ Como Compilar e Executar no Terminal do VS Code

### Pré-requisitos
- **Java JDK 17** ou superior instalado e disponível no `PATH` (`java -version` e `javac -version`).
- **Visual Studio Code** (recomendado abrir a pasta raiz do projeto e abrir o Terminal Integrado com ``Ctrl + '`` ou `Ctrl + Shift + P` → `Terminal: Create New Terminal`).

### Opção 1: Execução Rápida pelo Script (`executar.bat`)
No terminal do VS Code (PowerShell ou CMD), execute na raiz do projeto:

```bat
.\executar.bat
```

Para rodar diretamente a **Demonstração Automática Guiada** (que executa passo a passo o Fluxo Principal e exercita **FA1**, **FA2** e **FA3** automaticamente, exibindo ao final todas as tabelas CSV geradas):

```bat
.\executar.bat --demo
```

### Opção 2: Compilação e Execução Manual com `javac` e `java`

**No PowerShell (Windows):**
```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
New-Item -ItemType Directory -Force -Path bin | Out-Null
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src/*.java).FullName
java -cp bin Main
```

**No Prompt de Comando (CMD) ou Linux/macOS:**
```bash
mkdir bin
javac -encoding UTF-8 -d bin src/entity/*.java src/service/*.java src/collection/*.java src/controller/*.java src/boundary/*.java src/Main.java
java -cp bin Main
```

### Opção 3: Pelas Tasks / Launch do VS Code
- Pressione `Ctrl + Shift + B` no VS Code para acionar a task padrão **"Compilar e Executar Minha Refeicao (Terminal)"**, ou pressione `F5` na classe `src/Main.java`.

---

## 🧪 Como Exercitar o Fluxo Principal e os Fluxos Alternativos no Terminal

Ao iniciar o sistema (`java -cp bin Main`), o menu principal apresenta três opções:
1. **`[1] Executar Contratação Interativa (Telas 01 a 15 no Terminal)`**
2. **`[2] Executar Demonstração Guiada Automática (Fluxo Principal + FA1, FA2 e FA3)`**
3. **`[3] Consultar Dados Persistidos nos Arquivos CSV (pasta dados/)`**

Durante a **Contratação Interativa (`[1]`)**, você pode testar cada fluxo da seguinte forma:

- **Fluxo Alternativo 1 (`FA1 — Código SMS Inválido / Bloqueio / Reenvio`)**:
  - Na **Tela 02**, o sistema exibe no console a simulação do SMS enviado com o código de 4 dígitos (ex.: `4829`).
  - Digite um código incorreto (ex.: `0000`) até **3 vezes** para verificar o incremento de `tentativasInvalidas` e o bloqueio automático (`SituacaoCodigo.BLOQUEADO`), exigindo emissão de novo código com contador zerado.
  - Ou digite `R` enquanto o código estiver `PENDENTE` para testar o reenvio e verificar no arquivo `dados/codigos_confirmacao.csv` que o código anterior passa para `SUBSTITUIDO`.
- **Fluxo Alternativo 2 (`FA2 — Seleção Incompatível ou Ajuste no Resumo`)**:
  - Nas **Telas 07 a 09**, pressione `ENTER` sem selecionar itens (seleção vazia), informe uma quantidade de **Pratos Principais** acima do limite do plano (ex.: `1:4, 2:3` = 7 refeições em um Plano Semanal de 5 refeições), ou digite `EXP` para tentar selecionar um item de cardápio fora de vigência. O método `Pedido.validarSelecao` recusará a seleção (`false`), preservando os itens válidos anteriores.
  - Na **Tela 10 (Resumo do Pedido)**, escolha a opção `[2] Ajustar composição do pedido inicial (FA2)` para voltar à seleção, alterar quantidades de itens existentes (`ItemPedido.alterarQuantidade`), adicionar novos itens ou remover itens desmarcados (`Pedido.removerItem`).
- **Fluxo Alternativo 3 (`FA3 — Pagamento Não Autorizado pela Operadora`)**:
  - Na **Tela 13 (Pagamento)**, informe o CVV `000` ou um número de cartão terminado em `0000`. A `OperadoraCartao` retornará `SituacaoPagamento.RECUSADO`.
  - A tentativa será registrada como `RECUSADO` em `dados/pagamentos.csv`, a assinatura permanecerá em `AGUARDANDO_PAGAMENTO`, e você poderá escolher `[1] Tentar novamente / Usar outro cartão` (informando p.ex. CVV `123` para aprovar) ou `[2] Encerrar contratação sem pagamento autorizado`.
