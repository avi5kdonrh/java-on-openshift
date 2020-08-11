package org.example;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;

public class RunIt {

    public static void main(String[] args) throws Exception {
        if(args.length < 6){
            System.out.println(">> Invalid arg length ");
            System.out.println(">> Order >> url: user: password: transacted(true/false): destination: messageCount: TextMessage");
            System.exit(0);
        }
      String url = args[0];
      String user = args[1];
      String password = args[2];
      Boolean transacted = false;
      String queueName = "TEST";
      String topicName = "TEST";
      Boolean queue = true;
       transacted =  Boolean.valueOf(args[3]);
          if("topic".equals(args[4].split(":")[0])){
            queue = false;
          }
       if(queue && args.length>=4 && args[4].contains(":")){
        queueName = args[4].split(":")[1];
       }else if(queue && args.length>=4 && !args[4].contains(":")){
        queueName = args[4];
       }else {
           topicName = args[4].split(":")[1];
       }
      int messageCount = args[5] != null ? Integer.valueOf(args[5]) : 1;
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(url,user,password);
        Connection connection = null;
        Session session = null;

        try {
            connection = activeMQConnectionFactory.createConnection();
            if (transacted)
                session = connection.createSession(transacted, Session.SESSION_TRANSACTED);
            else
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = null;
            if (queue) {
                destination = session.createQueue(queueName);
            } else {
                destination = session.createTopic(topicName);
            }
            MessageProducer messageProducer = session.createProducer(destination);
            Message message = session.createTextMessage(args[6] != null ? args[6] : "This is a test message");
            System.out.println(">> Sending Messages >>");
            for (int i = 0; i < messageCount; i++) {
                messageProducer.send(message);
            }
            if (transacted) {
                session.commit();
            }
            System.out.println(">> Messages Sent: "+messageCount);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                connection.close();
            }
        }

    }
}
