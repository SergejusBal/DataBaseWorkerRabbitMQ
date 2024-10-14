package org.example;

import org.example.Image.ImageTableWorkers;
import org.example.Mail.MailTableWorkers;

public class Main {
    public static void main(String[] args) {

        MailTableWorkers mailTableWorkers = new MailTableWorkers();
        Thread thread01 = new Thread(mailTableWorkers);
        thread01.start();

        ImageTableWorkers imageTableWorkers = new ImageTableWorkers();
        Thread thread02 = new Thread(imageTableWorkers);
        thread02.start();


    }
}