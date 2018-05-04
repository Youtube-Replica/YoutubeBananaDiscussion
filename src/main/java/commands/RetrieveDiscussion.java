package commands;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import model.Discussion;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class RetrieveDiscussion extends Command {
   public void execute() {
       HashMap<String, Object> props = parameters;

       Channel channel = (Channel) props.get("channel");
       JSONParser parser = new JSONParser();
       int id = 0;
       int video_id = 0;
       boolean flag = false;
       try {
           JSONObject body = (JSONObject) parser.parse((String) props.get("body"));
           System.out.println("******xxx******");
           JSONObject params = (JSONObject) parser.parse(body.get("parameters").toString());
           System.out.println(params.toString());
           if(params.containsKey("video_id")){
               video_id = Integer.parseInt(params.get("video_id").toString());
               flag = true;
           }
           else{
               id = Integer.parseInt(params.get("id").toString());
           }
       } catch (ParseException e) {
           e.printStackTrace();
       }
       System.out.println("Passed!");
       AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
       AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
       Envelope envelope = (Envelope) props.get("envelope");
       String response = "";
       if(flag){
           System.out.println("Get comments by CHANNEL ID");
           response = Discussion.getDiscussionByChannelID(video_id);
       }
       else{
           response = Discussion.getDiscussionByID(id);
       }
       try {
           channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

}
