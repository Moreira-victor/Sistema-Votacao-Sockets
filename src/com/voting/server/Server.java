package com.voting.server;

import com.voting.common.ElectionData;
import com.voting.common.Vote;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lógica principal do servidor.
 * Agora registra a DATA e HORA de cada voto.
 */
public class Server implements Runnable {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private boolean isRunning = true;
    private final ServerGUI gui;

    private final ElectionData electionData;
    private final Map<String, Integer> voteCounts = new ConcurrentHashMap<>();
    
    // MUDANÇA PRINCIPAL: Map<CPF, DataHora> em vez de Set<CPF>
    // ConcurrentHashMap garante segurança em multithreading
    private final Map<String, String> votedCPFs = new ConcurrentHashMap<>();

    public Server(ServerGUI gui) {
        this.gui = gui;
        String question = "Qual a melhor linguagem de programacao?";
        List<String> options = Arrays.asList("Java", "Python", "JavaScript", "C++");
        
        this.electionData = new ElectionData(question, options);
        options.forEach(option -> voteCounts.put(option, 0));
        updateGUI();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // new Thread para cada cliente (Multithreading)
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (SocketException e) {
                    if (!isRunning) System.out.println("Server socket closed.");
                    else e.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (isRunning) e.printStackTrace();
        } finally {
            stopServer();
        }
    }
    
    public synchronized void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void processVote(Vote vote) {
        // Bloco sincronizado para garantir integridade dos dados (Thread Safety)
        synchronized (this) {
            // Verifica se a CHAVE (CPF) já existe
            if (votedCPFs.containsKey(vote.getCpf())) {
                throw new IllegalStateException("This CPF has already voted.");
            }
            if (!voteCounts.containsKey(vote.getChosenOption())) {
                 throw new IllegalArgumentException("Invalid voting option.");
            }
            
            // Captura a data e hora atual
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            
            // Salva no mapa: CPF -> Data/Hora
            votedCPFs.put(vote.getCpf(), timeStamp);
            
            voteCounts.compute(vote.getChosenOption(), (key, val) -> val + 1);
            System.out.println("Vote received from CPF " + vote.getCpf() + " at " + timeStamp);
            
            updateGUI();
        }
    }
    
    private void updateGUI() {
        int totalVotes = votedCPFs.size();
        // Envia o Map completo para a GUI
        gui.updateResults(electionData.getQuestion(), voteCounts, totalVotes, votedCPFs);
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket socket) { this.clientSocket = socket; }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                
                // 1. Envia dados da eleição
                out.writeObject(electionData);
                out.flush();
                
                // 2. Recebe o voto
                Vote vote = (Vote) in.readObject();
                
                // 3. Processa
                try {
                    processVote(vote);
                    out.writeObject("SUCCESS: Your vote has been registered.");
                } catch (Exception e) {
                    out.writeObject("ERROR: " + e.getMessage());
                }
                out.flush();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}