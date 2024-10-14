package org.example.Mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MailTableWorkers implements Runnable{

    private final String QUEUENAME = "Mail";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;

    public MailTableWorkers() {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {

        try {
            receiveAndProcessOneMessageAtATime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void receiveAndProcessOneMessageAtATime() throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUENAME , false, false, false, null);

            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String jsonMessage = new String(delivery.getBody(), "UTF-8");

                try {
                    Mail mail = objectMapper.readValue(jsonMessage, Mail.class);

                    MailRepository mailRepository = new MailRepository();
                    String dataBaseResponse = mailRepository.registerMail(mail);
                    SendMail sendMail = new SendMail();
                    String sendGrindResponse = sendMail.sendMail(mail.getEmailTo(),"Test RabbitMQ",mail.getContent());

                    System.out.println("Worker Thread: " + Thread.currentThread().getName() + " || MySQL Info: " + dataBaseResponse + " || Mail info: " + sendGrindResponse);

                    //Patvirtinimas jog žinutė apdorota sėkmingai
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (Exception e) {

                    e.printStackTrace();

                    //Praneša, jog nepavyko apdoroti žinutės ir žinutė vėl grąžinama į eilę
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };

            channel.basicConsume(QUEUENAME , false, deliverCallback, consumerTag -> {});

            System.out.println("Mail Channel Open.");
            while (true) {
                Thread.sleep(1000);
            }
        }
    }

}
