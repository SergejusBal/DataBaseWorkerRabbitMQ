package org.example.Mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.example.DataBaseInfo;

import java.io.IOException;

public class SendMail {

    public String sendMail(String sendTo, String title, String sendContent) {
        Email from = new Email("sergejus.balciunas@gmail.com");
        Email to = new Email(sendTo);
        Content content = new Content("text/plain", sendContent);
        Mail mail = new Mail(from, title, to, content);

        SendGrid sg = new SendGrid(DataBaseInfo.SENDGRID);
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sg.api(request);
        //    System.out.println("Status code: "  + response.getStatusCode());

        } catch (IOException ex) {
            // throw ex;
        }
        return "SendGrind status code: " + response.getStatusCode();
    }
}
