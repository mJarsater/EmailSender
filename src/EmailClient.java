import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


public class EmailClient extends JFrame  {
    private JFrame jFrame;
    private JTextField serverField,usernameField, passwordField,
            fromField, toField, subjectField, messegeField;
    private JLabel serverLabel, usernameLabel, passwordLabel,
            fromLabel, toLabel, subjectLabel, statusLabel;
    private JButton sendBtn, outlookBtn, googleBtn;
    private String username, password, server, toAdress, fromAdress,subject, msg;

    public EmailClient(){
            this.jFrame = this;
            this.setTitle("Email client");
            sendBtn = new JButton("Send");
            outlookBtn = new JButton("Outlook server");
            googleBtn = new JButton("Gmail server");
            serverField = new JTextField();
            usernameField = new JTextField();
            passwordField = new JTextField();
            fromField = new JTextField();
            toField = new JTextField();
            subjectField = new JTextField();
            messegeField = new JTextField();
            serverLabel = new JLabel("Server");
            usernameLabel = new JLabel("Username");
            passwordLabel = new JLabel("Password");
            statusLabel = new JLabel("Status: Waiting...");
            fromLabel = new JLabel("From");
            toLabel = new JLabel("To");
            subjectLabel = new JLabel("Subject");
            messegeField = new JTextField();
            sendBtn = new JButton("Send");


            // -------- SET BOUNDS ------------
            serverLabel.setBounds(10,10,80, 20);
            outlookBtn.setBounds(260, 10, 140, 30);
            googleBtn.setBounds(410, 10, 140, 30);
            statusLabel.setBounds(260 ,50,350,20);
            usernameLabel.setBounds(10,40,80, 20);
            passwordLabel.setBounds(10,70,80, 20);
            serverField.setBounds(100,10,100,20);
            usernameField.setBounds(100,40,100,20);
            passwordField.setBounds(100,70,100,20);
            fromLabel.setBounds(50, 130, 50, 20);
            toLabel.setBounds(250, 130, 100, 20);
            subjectLabel.setBounds(50, 150, 50, 20);
            fromField.setBounds(100,130, 100, 20);
            toField.setBounds(280,130, 100, 20);
            subjectField.setBounds(100,150, 100, 20);
            messegeField.setBounds(100,210, 100, 20);
            sendBtn.setBounds(100,240,100, 40);



            // ----------- ADD ----------------

            this.add(serverLabel);
            this.add(outlookBtn);
            this.add(googleBtn);
            this.add(statusLabel);
            this.add(usernameLabel);
            this.add(passwordLabel);
            this.add(serverField);
            this.add(usernameField);
            this.add(passwordField);
            this.add(fromLabel);
            this.add(toLabel);
            this.add(subjectLabel);
            this.add(fromField);
            this.add(toField);
            this.add(subjectField);
            this.add(messegeField);
            this.add(sendBtn);
            sendBtn.addActionListener(this::sendAction);
            outlookBtn.addActionListener(this::outlookAction);
            googleBtn.addActionListener(this::googleAction);


            this.setSize(600,600);
            this.setLayout(null);
            this.setVisible(true);


    }


    public synchronized boolean checkInput(){
        if(serverField.getText().isBlank()
                || usernameField.getText().isBlank()
                || passwordField.getText().isBlank()
                || fromField.getText().isBlank()
                || toField.getText().isBlank()
                || subjectField.getText().isBlank()
                || messegeField.getText().isBlank()){
            return false;
        } else {
            return true;
    }
    }

    public void clearInput(){
        serverField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        fromField.setText("");
        toField.setText("");
        subjectField.setText("");
        messegeField.setText("");
    }

    public void setStatus(String newStatus){
        statusLabel.setText(newStatus);
    }

    public void outlookAction(ActionEvent e){
        serverField.setText("");
        serverField.setText("smtp-mail.outlook.com");
    }

    public void googleAction(ActionEvent e){
        serverField.setText("");
        serverField.setText("smtp.gmail.com");
    }

    public void sendAction(ActionEvent e) {
        boolean checkText = checkInput();
        if (checkText) {
            server = serverField.getText();
            username = usernameField.getText();
            password = passwordField.getText();
            toAdress = toField.getText();
            fromAdress = fromField.getText();
            subject = subjectField.getText();
            msg = messegeField.getText();
            Email email = new Email(this, username, password, server, toAdress, fromAdress, subject, msg);
            email.createSession();
        } else  {
            setStatus("Status: Could not send email.");

        }
    }


// -------------- MAIN ---------------------------
    public static void main(String[] args) {
        EmailClient emailClient = new EmailClient();
    }
}

class Email {
    EmailClient emailClient;
    Properties properties;
    String username, password, server, toAdress,fromAdress, subject, msg;
    Session session;
    Message message;

    public Email(EmailClient emailClient,String username, String password, String server, String toAdress,String fromAdress, String subject, String messege){
        properties = new Properties();
        this.server = server;
        this.fromAdress = fromAdress;
        properties.put("mail.smtp.host",server);
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth","true");
        this.username = username;
        this.password = password;
        this.server = server;
        this.toAdress = toAdress;
        this.subject = subject;
        this.msg = messege;
        this.emailClient = emailClient;
    }




    public void setErrorMessage(){
        emailClient.setStatus("Status: Email sent succesfully!");
    }

    public void setSuccessMessage(){
        emailClient.setStatus("Status: Email sent succesfully!");
    }

    public void createSession(){
        emailClient.setStatus("Status: Sending email....");
        session =  Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        });
        createMessege();
    }



    public void createMessege() throws NullPointerException{
        message = prepMessege();
        try{
        Transport.send(message);
        setSuccessMessage();
        emailClient.clearInput();
    } catch (MessagingException me){
            setErrorMessage();
        }
    }

    public Message prepMessege() throws NullPointerException{
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, fromAdress));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAdress));
            message.setSubject(subject);
            message.setText(msg);
            return message;
        } catch (AddressException ae) {
            setErrorMessage();
            System.out.print(ae);
        } catch (MessagingException me){
            setErrorMessage();
            System.out.print(me);
        } catch (UnsupportedEncodingException uee){
            setErrorMessage();
            System.out.print(uee);
        }
        return null;
    }

}
