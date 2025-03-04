package com.example.Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientMain {
    private static JTextArea chatArea;
    private static JTextField inputField;
    private static PrintWriter out;
    private static String username;

    public static void main(String[] args) {
        Socket clientSocket;

        // Creazione dell'interfaccia grafica del client
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        // Creazione della chat area con uno stile più moderno
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(new Color(240, 240, 240));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Pannello di input con una migliore disposizione e un'area più visibile
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setPreferredSize(new Dimension(300, 30));

        JButton sendButton = new JButton("Invia");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(50, 150, 250));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createLineBorder(new Color(30, 120, 220), 2));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        try {
            // Connessione al server sulla porta 5500
            clientSocket = new Socket("localhost", 5500);
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Richiesta del nome utente tramite un popup
            username = JOptionPane.showInputDialog(frame, "Inserisci il tuo nome utente:");
            frame.setTitle("Chat di " + username);
            if (username != null && !username.trim().isEmpty()) {
                out.println(username); // Invia il nome utente al server
                chatArea.append("Nome utente impostato: " + username + "\n");
            }

            // Avvio del thread per la ricezione dei messaggi
            Thread riceviThread = new Thread(new ThreadRicevi(clientSocket, chatArea));
            riceviThread.start();

            // Definizione dell'azione per inviare messaggi
            ActionListener sendAction = e -> {
                String message = inputField.getText().trim();
                if (!message.isEmpty()) {
                    out.println(message); // Invia il messaggio al server
                    chatArea.append("Tu: " + message + "\n");
                    inputField.setText("");
                }
            };

            // Collegamento del listener ai componenti UI
            sendButton.addActionListener(sendAction);
            inputField.addActionListener(sendAction);

        } catch (IOException e) {
            // Gestione errore in caso di problemi di connessione
            chatArea.append("Errore di connessione al server\n");
        }
    }
}
   