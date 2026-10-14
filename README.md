# Sistema de Votação Distribuído com Sockets

![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Socket](https://img.shields.io/badge/Socket-TCP/IP-00599C?style=for-the-badge)

Sistema de Votação Distribuído baseado na arquitetura Cliente-Servidor, implementado em Java utilizando a API de Sockets TCP/IP para comunicação confiável entre aplicações.

## 📋 Índice

- [Sobre](#-sobre)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Como Usar](#-como-usar)
- [Protocolo de Comunicação](#-protocolo-de-comunicação)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Testes](#-testes)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)

## 📖 Sobre

Este projeto consiste no desenvolvimento de um Sistema de Votação Distribuído baseado na arquitetura Cliente-Servidor, implementado na linguagem Java. O sistema utiliza a API de Sockets TCP/IP para estabelecer comunicação confiável entre as aplicações.

### 🎯 Objetivo

Demonstrar conceitos de programação distribuída, comunicação via sockets, e implementação de sistemas de votação em tempo real, permitindo múltiplos clientes votarem simultaneamente em um servidor centralizado.

## ✨ Funcionalidades

### Servidor
- ✅ Aceita conexões de múltiplos clientes simultaneamente
- ✅ Gerencia sessões de votação
- ✅ Processa e contabiliza votos em tempo real
- ✅ Transmite resultados atualizados para todos os clientes
- ✅ Log de todas as operações
- ✅ Suporte a múltiplas opções de votação

### Cliente
- ✅ Conecta-se ao servidor via TCP/IP
- ✅ Exibe opções de votação disponíveis
- ✅ Envia votos ao servidor
- ✅ Recebe atualizações de resultados em tempo real
- ✅ Interface de linha de comando (CLI)
- ✅ Tratamento de erros de conexão

## 🏗️ Arquitetura

O sistema segue a arquitetura **Cliente-Servidor** clássica:

```
┌─────────────┐     TCP/IP     ┌─────────────┐
│   Cliente 1 │ ◄─────────────► │             │
├─────────────┤                │             │
│   Cliente 2 │ ◄─────────────► │   Servidor  │
├─────────────┤                │             │
│   Cliente 3 │ ◄─────────────► │             │
└─────────────┘                └─────────────┘
```

### Fluxo de Comunicação

1. **Cliente** se conecta ao **Servidor**
2. **Servidor** envia opções de votação disponíveis
3. **Cliente** seleciona e envia voto
4. **Servidor** processa e atualiza contagem
5. **Servidor** broadcast resultados para todos clientes
6. **Cliente** exibe resultados atualizados

## 💻 Tecnologias

- **Java** - Linguagem de programação principal
- **Java Sockets API** - Comunicação TCP/IP
- **Java I/O** - Manipulação de streams
- **Multithreading** - Concorrência de clientes
- **Collections Framework** - Gerenciamento de dados

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) (Java 11 ou superior)
- [Apache Maven](https://maven.apache.org/) ou [Gradle](https://gradle.org/) (opcional)
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)
- Terminal/CMD

## 🚀 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/Moreira-victor/Sistema-Votacao-Sockets.git
cd Sistema-Votacao-Sockets
```

### 2. Compile o Projeto

```bash
# Se usando Maven
mvn clean compile

# Se usando javac diretamente
javac src/main/java/com/votacao/*.java
```

### 3. Configuração

Edite o arquivo de configuração para definir:

- Porta do servidor (padrão: 8080)
- Endereço IP do servidor (padrão: localhost)
- Opções de votação disponíveis

## 💻 Como Usar

### Iniciar o Servidor

```bash
# Maven
mvn exec:java -Dexec.mainClass="com.votacao.Servidor"

# Java direto
java -cp target/classes com.votacao.Servidor
```

O servidor iniciará e aguardará conexões na porta configurada.

### Iniciar um Cliente

```bash
# Maven
mvn exec:java -Dexec.mainClass="com.votacao.Cliente"

# Java direto
java -cp target/classes com.votacao.Cliente
```

Você pode iniciar múltiplos clientes em terminais diferentes para simular a votação distribuída.

### Exemplo de Uso

```
Cliente 1:
> Conectado ao servidor localhost:8080
> Opções disponíveis:
>   1. Opção A
>   2. Opção B
>   3. Opção C
> Digite sua escolha: 1
> Voto registrado com sucesso!
> Resultados atuais: A: 1, B: 0, C: 0

Cliente 2:
> Conectado ao servidor localhost:8080
> Opções disponíveis:
>   1. Opção A
>   2. Opção B
>   3. Opção C
> Digite sua escolha: 2
> Voto registrado com sucesso!
> Resultados atuais: A: 1, B: 1, C: 0
```

## 📡 Protocolo de Comunicação

O sistema utiliza um protocolo simples baseado em mensagens de texto:

### Formato das Mensagens

**Cliente → Servidor:**
- `VOTO:<opção>` - Envia um voto
- `DESCONECTAR` - Encerra conexão

**Servidor → Cliente:**
- `OPCOES:<lista>` - Envia opções disponíveis
- `RESULTADOS:<dados>` - Envia resultados atualizados
- `SUCESSO` - Confirmação de operação
- `ERRO:<mensagem>` - Mensagem de erro

### Exemplo de Mensagem

```
Cliente: VOTO:1
Servidor: SUCESSO
Servidor: RESULTADOS:A:1,B:0,C:0
```

## 🧪 Testes

### Teste de Conexão Simples

```bash
# Terminal 1 - Iniciar servidor
java com.votacao.Servidor

# Terminal 2 - Iniciar cliente
java com.votacao.Cliente
```

### Teste de Múltiplos Clientes

```bash
# Terminal 1 - Servidor
java com.votacao.Servidor

# Terminal 2, 3, 4, ... - Clientes
java com.votacao.Cliente
```

### Teste de Tolerância a Falhas

- Desconecte o servidor enquanto clientes estão conectados
- Reconecte o servidor e verifique recuperação
- Teste com conexões simultâneas de 10+ clientes

## 📁 Estrutura do Projeto

```
Sistema-Votacao-Sockets/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── votacao/
│                   ├── Servidor.java      # Servidor principal
│                   ├── Cliente.java       # Cliente
│                   ├── GerenciadorVotos.java # Lógica de votação
│                   └── Utils.java         # Utilitários
├── target/              # Arquivos compilados
├── pom.xml             # Configuração Maven (se aplicável)
└── README.md           # Este arquivo
```

## 🔧 Configuração Avançada

### Alterar Porta do Servidor

```java
// Em Servidor.java
int porta = 9090; // Altere para a porta desejada
```

### Adicionar Novas Opções de Votação

```java
// Em GerenciadorVotos.java
adicionarOpcao("Nova Opção");
```

### Configurar Timeout de Conexão

```java
// Em Cliente.java
socket.setSoTimeout(5000); // 5 segundos
```

## 🐛 Troubleshooting

### Erro: "Porta já em uso"
```bash
# Verifique processos usando a porta (Linux/Mac)
lsof -i :8080

# Ou use outra porta no código
```

### Erro: "Conexão recusada"
- Verifique se o servidor está rodando
- Confirme o endereço IP e porta
- Verifique firewall/antivírus

### Cliente desconecta inesperadamente
- Verifique logs do servidor
- Confirme timeout configurado
- Teste estabilidade da rede

## 📝 Próximas Melhorias

- [ ] Interface gráfica (GUI) para clientes
- [ ] Persistência de resultados em banco de dados
- [ ] Autenticação de clientes
- [ ] Criptografia de comunicação (SSL/TLS)
- [ ] Suporte a votação ponderada
- [ ] Interface web para administração
- [ ] Dashboard de estatísticas em tempo real

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, siga estas etapas:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

### Diretrizes de Código

- Siga as convenções Java (Code Conventions)
- Adicione javadoc para classes e métodos públicos
- Trate exceções adequadamente
- Adicione testes unitários

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👤 Autor

**Victor Moreira**

- GitHub: [@Moreira-victor](https://github.com/Moreira-victor)
- LinkedIn: [victor-moreira-50b05620a](https://linkedin.com/in/victor-moreira-50b05620a)

## 📚 Conceitos Aprendidos

Este projeto demonstra conceitos importantes de:

- **Programação Distribuída** - Comunicação entre processos
- **Sockets TCP/IP** - Comunicação de rede confiável
- **Multithreading** - Concorrência em Java
- **Protocolos de Comunicação** - Design de protocolos simples
- **Tratamento de Exceções** - Gerenciamento de erros
- **Sincronização** - Acesso compartilhado a recursos

## 🙏 Agradecimentos

- Agradecimentos a documentação oficial Java
- Agradecimentos a recursos de aprendizado sobre sockets

---

⭐ Se você gostou deste projeto, considere dar uma estrela no GitHub!
