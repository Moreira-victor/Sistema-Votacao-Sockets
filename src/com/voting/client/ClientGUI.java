package com.voting.client;

import com.voting.common.ElectionData;
import com.voting.common.Vote;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.ParseException;

/**
 * A interface gráfica do cliente e sua lógica de rede.
 * Modificado: Campo de CPF só habilita após conexão.
 */
public class ClientGUI extends JFrame {
    private JTextField serverIpField, serverPortField;
    private JFormattedTextField cpfField; 
    private JButton connectButton, voteButton;
    private JPanel optionsPanel;
    private ButtonGroup optionsGroup;
    private JLabel questionLabel;
    
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    public ClientGUI() {
        setTitle("Distributed Voting Client - v1.30");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Painel de Conexão ---
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        serverIpField = new JTextField("127.0.0.1", 15);
        serverPortField = new JTextField("12345", 5);
        connectButton = new JButton("Connect");
        connectionPanel.add(new JLabel("Server IP:"));
        connectionPanel.add(serverIpField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(serverPortField);
        connectionPanel.add(connectButton);
        add(connectionPanel, BorderLayout.NORTH);

        // --- Painel de Votação ---
        JPanel votingPanel = new JPanel();
        votingPanel.setLayout(new BoxLayout(votingPanel, BoxLayout.Y_AXIS));
        votingPanel.setBorder(BorderFactory.createTitledBorder("Voting"));
        
        questionLabel = new JLabel("Please connect to the server to see the election question.");
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        
        // Configuração do Campo de CPF com Máscara
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfMask);
            cpfField.setColumns(15);
        } catch (ParseException e) {
            e.printStackTrace();
            cpfField = new JFormattedTextField();
        }
        
        // --- MODIFICAÇÃO AQUI: Começa Desabilitado ---
        cpfField.setEnabled(false); // Usuário não pode digitar ainda
        cpfField.setBackground(new Color(230, 230, 230)); // Cinza claro visual
        // ---------------------------------------------

        voteButton = new JButton("Cast Vote");
        voteButton.setEnabled(false); // Botão também começa travado
        
        votingPanel.add(questionLabel);
        votingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        votingPanel.add(optionsPanel);
        votingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel cpfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cpfPanel.add(new JLabel("Your CPF:"));
        cpfPanel.add(cpfField);
        votingPanel.add(cpfPanel);
        votingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        votingPanel.add(voteButton);
        
        add(votingPanel, BorderLayout.CENTER);
        
        createMenuBar();
        
        connectButton.addActionListener(e -> connectToServer());
        voteButton.addActionListener(e -> castVote());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Ajuda");

        JMenuItem helpItem = new JMenuItem("Como Votar");
        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Como Votar no Sistema:\n\n" +
                "1. Conecte-se ao servidor (IP e Porta) e clique em 'Connect'.\n" +
                "2. O campo de CPF será liberado.\n" +
                "3. Digite seu CPF, escolha a opção e clique em 'Cast Vote'.",
                "Ajuda", 
                JOptionPane.INFORMATION_MESSAGE));
        
        JMenuItem creditsItem = new JMenuItem("Créditos");
        creditsItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Projeto Votação Distribuída v1.00\n\n" +
                "Desenvolvido por:\n" +
                "Vitor Carneiro Borela (RA 260934)\n" +
                "Victor Moreira (RA 248679)",
                "Créditos", 
                JOptionPane.INFORMATION_MESSAGE));
        
        helpMenu.add(helpItem);
        helpMenu.add(creditsItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    private void connectToServer() {
        try {
            socket = new Socket(serverIpField.getText(), Integer.parseInt(serverPortField.getText()));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            ElectionData electionData = (ElectionData) in.readObject();
            
            questionLabel.setText("<html><center>" + electionData.getQuestion() + "</center></html>");
            optionsPanel.removeAll();
            optionsGroup = new ButtonGroup();
            for(String option : electionData.getOptions()) {
                JRadioButton radioButton = new JRadioButton(option);
                radioButton.setActionCommand(option);
                optionsGroup.add(radioButton);
                optionsPanel.add(radioButton);
            }
            
            // --- MODIFICAÇÃO AQUI: Habilita tudo após conectar ---
            connectButton.setEnabled(false); // Trava o botão de conectar
            serverIpField.setEditable(false); // Trava o IP
            
            voteButton.setEnabled(true); // Libera o botão de voto
            cpfField.setEnabled(true);   // Libera o campo de CPF
            cpfField.setBackground(Color.WHITE); // Volta a ficar branco
            cpfField.requestFocus();     // Já coloca o cursor lá pra digitar
            // -----------------------------------------------------

            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void castVote() {
        String cpf = cpfField.getText();
        
        // Validação ocorre aqui, no momento do clique
        if (!isCpfValid(cpf)) {
            JOptionPane.showMessageDialog(this, "CPF inválido ou incompleto.\nVerifique os dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (optionsGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma opção para votar.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String choice = optionsGroup.getSelection().getActionCommand();
            Vote vote = new Vote(cpf.replaceAll("[^\\d]", ""), choice);
            out.writeObject(vote);
            out.flush();
            String response = (String) in.readObject();
            JOptionPane.showMessageDialog(this, response, "Resposta do Servidor", JOptionPane.INFORMATION_MESSAGE);
            disconnect();
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Erro durante a votação: " + e.getMessage(), "Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) { /* Ignore */ } finally {
            // Trava tudo após o voto
            voteButton.setEnabled(false);
            cpfField.setEnabled(false);
            cpfField.setBackground(new Color(230, 230, 230));
        }
    }

    private boolean isCpfValid(String cpf) {
        String cleanCpf = cpf.replaceAll("[^\\d]", "");
        if (cleanCpf.length() != 11 || cleanCpf.matches("(\\d)\\1{10}")) return false;
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) sum += (cleanCpf.charAt(i) - '0') * (10 - i);
            int r = (sum * 10) % 11;
            if (r == 10) r = 0;
            if (r != (cleanCpf.charAt(9) - '0')) return false;
            sum = 0;
            for (int i = 0; i < 10; i++) sum += (cleanCpf.charAt(i) - '0') * (11 - i);
            r = (sum * 10) % 11;
            if (r == 10) r = 0;
            if (r != (cleanCpf.charAt(10) - '0')) return false;
        } catch (NumberFormatException e) { return false; }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}