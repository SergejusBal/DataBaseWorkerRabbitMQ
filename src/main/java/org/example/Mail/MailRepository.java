package org.example.Mail;

import org.example.DataBaseInfo;

import java.sql.*;
import java.time.LocalDateTime;

public class MailRepository {


    public String registerMail(Mail mail){

        if(mail == null || mail.getContent() == null || mail.getEmailTo() == null) return "Invalid data";

        String sql = "INSERT INTO mails (mail_to,content,sent) VALUES (?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(DataBaseInfo.URL, DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,mail.getEmailTo());
            preparedStatement.setString(2,mail.getContent());
            preparedStatement.setString(3, LocalDateTime.now().toString());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            return "Database connection failed";
        }

        return "Mail was successfully registered";

    }


}
