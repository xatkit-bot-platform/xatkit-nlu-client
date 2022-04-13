
import com.xatkit.core.XatkitBot;
import com.xatkit.core.recognition.IntentRecognitionProviderFactory;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.io.ReactEventProvider;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import lombok.val;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

import static com.xatkit.dsl.DSL.eventIs;
import static com.xatkit.dsl.DSL.fallbackState;
import static com.xatkit.dsl.DSL.intent;
import static com.xatkit.dsl.DSL.intentIs;
import static com.xatkit.dsl.DSL.mapping;
import static com.xatkit.dsl.DSL.model;
import static com.xatkit.dsl.DSL.state;


public class SampleBotWithNerTest {

    /*
     * Your bot is a plain Java application: you need to define a main method to make the created jar executable.
     */
    public static void main(String[] args) {


        val cityEntity = mapping("CityEntity")
                .entry()
                .value("Barcelona").synonym("BCN")
                .entry()
                .value("Madrid").synonym("MAD");

        val seasonEntity = mapping("SeasonEntity")
                .entry()
                .value("Summer")
                .entry()
                .value("Fall")
                .entry().value("Spring")
                .entry().value("Winter");

        val greetings = intent("Greetings")
                .trainingSentence("Hi")
                .trainingSentence("Hello")
                .trainingSentence("Good morning")
                .trainingSentence("Good afternoon");

        val howAreYou = intent("HowAreYou")
                .trainingSentence("How are you?")
                .trainingSentence("What's up?")
                .trainingSentence("How do you feel?");

        val canIVisit = intent("canIvisit")
                .trainingSentence("Can I visit in yourcity next thisseason?")
                .trainingSentence("Can I come to yourcity in thisseason?")
                .trainingSentence("I would love to visit you in yourcity")
                .trainingSentence("I feel it would be great to come to yourcity after thissesason")
                .parameter("city").fromFragment("yourcity").entity(cityEntity)
                .parameter("season").fromFragment("thisseason").entity(seasonEntity);

        val howAreYouResponse = intent("HowAreYouResponse")
                .trainingSentence("Great")
                .trainingSentence("Good")
                .trainingSentence("Fine");

        ReactPlatform reactPlatform = new ReactPlatform();
        ReactEventProvider reactEventProvider = new ReactEventProvider(reactPlatform);
        ReactIntentProvider reactIntentProvider = new ReactIntentProvider(reactPlatform);

        val init = state("Init");
        val awaitingInput = state("AwaitingInput");
        val handleWelcome = state("HandleWelcome");
        val handleWhatsUp = state("HandleWhatsUp");
        val handleVisit = state("HandleVisit");
        val handleHowAreYouResponse = state("HandleHowAreYouResponse");

        init
                .next()
                .when(eventIs(ReactEventProvider.ClientReady)).moveTo(awaitingInput);

        awaitingInput
                .next()
                .when(intentIs(greetings)).moveTo(handleWelcome)
                .when(intentIs(howAreYou)).moveTo(handleWhatsUp)
                .when(intentIs(canIVisit)).moveTo(handleVisit);


        handleWelcome
                .body(context -> reactPlatform.reply(context, "Hi, nice to meet you!"))
                .next()
                .moveTo(awaitingInput);

        handleWhatsUp
                .body(context -> reactPlatform.reply(context, "I am fine and you?"))
                .next()
                .when(intentIs(howAreYouResponse)).moveTo(handleHowAreYouResponse);

        handleHowAreYouResponse
                .body(context -> reactPlatform.reply(context, "Glad to hear that!"))
                .next()
                .moveTo(awaitingInput);


        handleVisit
                .body(context -> {
                    reactPlatform.reply(context,
                            "I'd love for you to visit me in " + context.getIntent().getValue(
                                    "city") + " next " + context.getIntent().getValue("season"));
                })
                .next()
                .moveTo(awaitingInput);

        val defaultFallback = fallbackState()
                .body(context -> reactPlatform.reply(context, "Sorry, I didn't get it"));

        val botModel = model()
                .usePlatform(reactPlatform)
                .listenTo(reactEventProvider)
                .listenTo(reactIntentProvider)
                .initState(init)
                .defaultFallbackState(defaultFallback);

        Configuration botConfiguration = new BaseConfiguration();
        botConfiguration.addProperty(IntentRecognitionProviderFactory.INTENT_PROVIDER_KEY,
                NLUServerConfiguration.NLUSERVER_INTENT_PROVIDER);
        botConfiguration.addProperty(NLUServerConfiguration.BOT_NAME, "SimpleBotWithNer");
        botConfiguration.addProperty(NLUServerConfiguration.URL, "http://127.0.0.1:8000");
        botConfiguration.addProperty(NLUServerConfiguration.FORCE_OVERWRITE, true);

        XatkitBot xatkitBot = new XatkitBot(botModel, botConfiguration);
        xatkitBot.run();

    }
}
