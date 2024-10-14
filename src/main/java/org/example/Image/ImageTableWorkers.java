package org.example.Image;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Mail.Mail;
import org.example.Mail.MailRepository;
import org.example.Mail.SendMail;

public class ImageTableWorkers implements Runnable{

    private final String QUEUENAME = "Image";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;


    public ImageTableWorkers() {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        while (true) {
            try {
                receiveAndProcessOneMessageAtATime();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void receiveAndProcessOneMessageAtATime() throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUENAME, false, false, false, null);

            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String jsonMessage = new String(delivery.getBody(), "UTF-8");

                try {
                    byte[] image = objectMapper.readValue(jsonMessage, byte[].class);

                    UploadImage uploadImage = new UploadImage();
                    String URL = uploadImage.uploadImage(image);

                    ImageReposiroty imageReposiroty = new ImageReposiroty();
                    String imageRepositoryResponse = imageReposiroty.registerImage(URL);

                    System.out.println("Worker Thread: " + Thread.currentThread().getName() + " || MySQL Info: " + imageRepositoryResponse+  " || ImageBB info: " + URL);

                    //Patvirtinimas jog žinutė apdorota sėkmingai
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (Exception e) {

                    e.printStackTrace();

                    //Praneša, jog nepavyko apdoroti žinutės ir žinutė vėl grąžinama į eilę
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };

            channel.basicConsume(QUEUENAME, false, deliverCallback, consumerTag -> {
            });

            System.out.println("Image Channel Open.");
            while (true) {
                Thread.sleep(1000);
            }
        }
    }
}
