package it.buffolollo;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Gui {
    private String title;
    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> lista;
    private JTextField textField;
    private JPanel panel;
    private JLabel label;
    private Client client;

    public Gui(String title) {
        this.title = title;
        initializeFrame();
    }

    private void initializeFrame() {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame
    }

    public void showConnectingToServer() {
        frame.getContentPane().removeAll(); // Clear the previous content

        frame.setLocationRelativeTo(null); // Center the frame

        JPanel connectingPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        connectingPanel.setBorder(new EmptyBorder(10, 50, 10, 50)); // Padding around the panel

        JLabel title = new JLabel("Connecting to server...");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        connectingPanel.add(title);

        frame.getContentPane().add(connectingPanel, BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    public void chooseLoginRegister() {
        frame.getContentPane().removeAll(); // Clear the previous content

        frame.setLocationRelativeTo(null); // Center the frame

        JPanel loginRegisterPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        loginRegisterPanel.setBorder(new EmptyBorder(10, 50, 10, 50)); // Padding around the panel

        JLabel title = new JLabel("Login or Register");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        loginRegisterPanel.add(title);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLogin();
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRegister();
            }
        });

        loginRegisterPanel.add(loginButton);
        loginRegisterPanel.add(new JLabel("")); // Spacer
        loginRegisterPanel.add(registerButton);

        frame.getContentPane().add(loginRegisterPanel, BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    private void showLogin() {
        // Clear the frame and display login fields
        frame.getContentPane().removeAll();

        frame.setLocationRelativeTo(null); // Center the frame

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        loginPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton submitButton = new JButton("Login");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = String.valueOf(passwordField.getPassword()).trim();

                App.login(username, password);
            }
        });
        loginPanel.add(submitButton);

        frame.getContentPane().add(loginPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void showRegister() {
        // Clear the frame and display registration fields
        frame.getContentPane().removeAll();

        frame.setLocationRelativeTo(null); // Center the frame

        JPanel registerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        registerPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        registerPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        registerPanel.add(usernameField);

        registerPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        registerPanel.add(passwordField);

        registerPanel.add(new JLabel("Confirm Password:"));
        JPasswordField confirmPasswordField = new JPasswordField();
        registerPanel.add(confirmPasswordField);

        JButton submitButton = new JButton("Register");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // handle registration logic
                String username = usernameField.getText().trim();
                String password = String.valueOf(passwordField.getPassword()).trim();
                String cpassword = String.valueOf(confirmPasswordField.getPassword()).trim();

                App.register(username, password, cpassword);
            }
        });
        registerPanel.add(submitButton);

        frame.getContentPane().add(registerPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void chatGui(Client c) {
        client = c;

        frame.setTitle(title + " - " + client.getID());

        frame.getContentPane().removeAll(); // Clear previous content

        frame.setSize(800, 600);

        panel = new JPanel(new BorderLayout());
        frame.add(panel);

        label = new JLabel("Welcome to Java Chat");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(10, 0, 20, 0));
        panel.add(label, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        lista = new JList<>();
        listModel = new DefaultListModel<>();
        lista.setModel(listModel);
        lista.setFont(new Font("Arial", Font.PLAIN, 16));
        scrollPane.setViewportView(lista);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSendMessage();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        bottomPanel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(Color.BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSendMessage();
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.setVisible(true);
    }

    public void makeErrorGui(String error) {
        frame.getContentPane().removeAll(); // Clear previous content

        frame.setSize(800, 300);
        frame.setLocationRelativeTo(null); // Center the frame

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel errorLabel = new JLabel(
                "<html><div style='text-align: center;'>" + error.replace("\n", "<br>") + "</div></html>");
        errorLabel.setFont(errorLabel.getFont().deriveFont(24.0f));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(errorLabel, BorderLayout.CENTER);

        if (!error.equals("Server is not available!")) {
            JButton button = new JButton("Retry");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooseLoginRegister();
                }
            });
            panel.add(button, BorderLayout.SOUTH);
        }

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // utilizzo la classe SwingUtilities per aggiungere un messaggio alla lista in
    // modo da evitare problemi di concorrenza
    // uso SwingUtilities per aggiornare i componenti dall'Event Dispatch Thread,
    // che è il thread principale per la gestione degli eventi in Swing
    // mi serve per non bloccare l'interfaccia grafica durante l'aggiunta di un
    // messaggio
    // https://docs.oracle.com/javase%2Ftutorial%2Fuiswing%2F%2F/concurrency/dispatch.html
    public void aggiungiMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listModel.addElement(msg);

                lista.ensureIndexIsVisible(listModel.getSize() - 1);
            }
        });
    }

    // uso dei thread per non bloccare l'interfaccia grafica durante l'invio di un
    // messaggio
    // evito di bloccare la gui inviando un messaggio
    private void handleSendMessage() {
        String msg = textField.getText().trim();

        if (!msg.isEmpty()) {
            Thread sendMessageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    App.sendMessage(msg);
                }
            });

            sendMessageThread.start();
        } else {
            aggiungiMsg("Error, message is empty!");
        }

        textField.setText("");
    }

}
