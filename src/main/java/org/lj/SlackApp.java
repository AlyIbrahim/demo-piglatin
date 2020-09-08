package org.lj;

import org.jboss.logging.Logger;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import javax.inject.Inject;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/slack/events")
public class SlackApp extends SlackAppServlet {
  private static final long serialVersionUID = 1L;
  
  private static final Logger LOG = Logger.getLogger(SlackApp.class);
  
  public SlackApp(App app) { super(app); }
  public SlackApp() throws IOException { 
    super(initSlackApp()); 
  }

  private App initSlackApp() throws IOException {
    App app = new App();
    app.command("/piglatin", (req, ctx) -> {
      
      //Translate the input text and set up the message
      PigLatin pigLatin = new PigLatin();
      String textToTranslate = req.getPayload().getText();
      SlackMessage message = new SlackMessage();
      message.text = pigLatin.translateToPigLatin(textToTranslate);
      LOG.info(textToTranslate + " translated to " + message.text);
      
      //Send result to Kafka
      KafkaHelper.sendToKafka(message);
      
//      TranslatorResource translator;
  //    translator.text = translatedText;
    //  translator.addTranslation();
      
      //Tell Slack we got the message.
      return ctx.ack(message.text);
    });
    return app;
  }
}
