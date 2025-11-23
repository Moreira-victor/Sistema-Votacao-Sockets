Sistema de Votação Distribuído

Este projeto é uma implementação de uma urna eletrônica em rede utilizando **Java Sockets**. O sistema permite que múltiplos eleitores votem simultaneamente através de uma arquitetura Cliente-Servidor segura e eficiente.

## 🚀 Tecnologias Utilizadas
* **Linguagem:** Java
* **Comunicação:** Sockets TCP/IP
* **Interface:** Java Swing (GUI)
* **Transmissão de Dados:** Serialização de Objetos

## ⚙️ Funcionalidades
* **Servidor Multithreaded:** Gerencia múltiplas conexões de eleitores ao mesmo tempo.
* **Cliente Gráfico:** Interface amigável para digitação de CPF e escolha de candidatos.
* **Segurança de Dados:** Integridade garantida através de `ObjectOutputStream`.
* **Apuração em Tempo Real:** O servidor exibe o log e a contagem de votos instantaneamente.

## 📦 Como Executar
1.  Execute primeiro a classe `ServerGUI` (Lado do Servidor).
2.  Em seguida, execute quantas instâncias desejar do `ClientGUI` (Lado do Cliente).
3.  No servidor, inicie a eleição. No cliente, conecte-se e vote.
