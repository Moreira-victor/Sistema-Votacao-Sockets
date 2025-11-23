package com.voting.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * A interface gráfica para o servidor.
 * Exibe resultados, auditoria com DATA/HORA e botão de encerramento corrigido.
 */
public class ServerGUI extends JFrame {
    private JTextArea resultsArea;
    private JTextArea logArea;
    private Server server;
    private JButton endButton;

    public ServerGUI() {
        setTitle("Election Server Status - v1.40 (Audited)");
        setSize(650, 550); // Um pouco mais largo para caber a data
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Monitoramento da Eleição em Tempo Real", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultsArea.setBorder(BorderFactory.createTitledBorder("Contagem de Votos"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setForeground(new Color(0, 100, 0));
        logArea.setBorder(BorderFactory.createTitledBorder("Auditoria de Votos (CPF | Data)"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                new JScrollPane(resultsArea), 
                new JScrollPane(logArea));
        splitPane.setDividerLocation(220);
        splitPane.setResizeWeight(0.5);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // --- Botão de Encerrar (Visual Corrigido) ---
        endButton = new JButton("Encerrar Votação e Gerar Relatório");
        endButton.setFont(new Font("Arial", Font.BOLD, 14));
        endButton.setForeground(Color.BLACK); 
        endButton.setBackground(new Color(255, 100, 100)); 
        endButton.setOpaque(true);
        endButton.setBorderPainted(false);
        
        endButton.addActionListener(e -> encerrarVotacao());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(endButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        createMenuBar();

        server = new Server(this);
        new Thread(server).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               confirmarSaida();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Ajuda");
        
        JMenuItem aboutItem = new JMenuItem("Sobre");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Sistema de Votação Distribuída v1.40\nCom Auditoria Temporal.", 
                "Sobre", JOptionPane.INFORMATION_MESSAGE));
        
        JMenuItem creditsItem = new JMenuItem("Créditos");
        creditsItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Projeto Votação Distribuída\n\n" +
                "Desenvolvido por:\n" +
                "Vitor Carneiro Borela (RA 260934)\n" +
                "Victor Moreira (RA 248679)", 
                "Créditos", JOptionPane.INFORMATION_MESSAGE));
        
        helpMenu.add(aboutItem);
        helpMenu.add(creditsItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    // ATUALIZADO: Recebe Map<String, String> votedData
    public void updateResults(String question, Map<String, Integer> voteCounts, int totalVotes, Map<String, String> votedData) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sbResults = new StringBuilder();
            sbResults.append(question).append("\n");
            sbResults.append("----------------------------------\n");
            for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
                sbResults.append(String.format("%-20s: %d votos\n", entry.getKey(), entry.getValue()));
            }
            sbResults.append("----------------------------------\n");
            sbResults.append(String.format("Total de Votos: %d\n", totalVotes));
            resultsArea.setText(sbResults.toString());

            StringBuilder sbLog = new StringBuilder();
            // Itera sobre o Mapa para pegar CPF e DATA
            for (Map.Entry<String, String> entry : votedData.entrySet()) {
                String rawCpf = entry.getKey();
                String time = entry.getValue();
                
                // Formatação visual do CPF
                String formattedCpf = rawCpf.length() == 11 
                    ? rawCpf.substring(0,3) + "." + rawCpf.substring(3,6) + "." + rawCpf.substring(6,9) + "-" + rawCpf.substring(9,11)
                    : rawCpf;
                
                sbLog.append("CPF: ").append(formattedCpf)
                     .append("  |  Data: ").append(time)
                     .append("\n");
            }
            logArea.setText(sbLog.toString());
        });
    }

    private void encerrarVotacao() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja encerrar a votação?", 
                "Confirmar Encerramento", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            server.stopServer();
            String relatorio = "=== RELATÓRIO FINAL DA ELEIÇÃO ===\n\n" + 
                               resultsArea.getText() + "\n" +
                               "=== AUDITORIA DETALHADA ===\n" +
                               logArea.getText();

            JTextArea textArea = new JTextArea(relatorio);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Relatório Final", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void confirmarSaida() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja sair sem encerrar formalmente?", 
                "Sair", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            server.stopServer();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}