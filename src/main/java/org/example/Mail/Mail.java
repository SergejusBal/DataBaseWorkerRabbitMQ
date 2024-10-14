package org.example.Mail;

public class Mail {

    private String emailTo;
    private String content;

    public Mail() {
    }

    public String getEmailTo() {
        return emailTo;
    }

    public String getContent() {
        return content;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "emailTo='" + emailTo + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
