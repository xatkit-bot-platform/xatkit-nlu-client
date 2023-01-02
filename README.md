Xatkit NLU Engine connector
======
This is the client connector for [Xatkit's own NLU Server](https://github.com/xatkit-bot-platform/xatkit-nlu-server) provider

Similar to the other [Intent Recognition](https://github.com/xatkit-bot-platform/xatkit/wiki/Intent-Recognition-Providers) connectors the goal of this repo is to enable 
Xatkit bots to use a specific Intent Recognition Provider, in this case Xatkit's NLU Server.


# Enabling the intent provider
To use Xatkit NLU Engine in your bot, start by [installing and deploying the NLU Server component](https://github.com/xatkit-bot-platform/xatkit-nlu-server). 
You can run it locally (by default it will run on `http://127.0.0.1:8000`. 

Obviously, [Xatkit itself also needs to be installed](https://github.com/xatkit-bot-platform/xatkit/wiki/Build-Xatkit).

Then, add the following dependency in your bot's `pom.xml` file:

```xml
<dependency>
    <groupId>com.xatkit</groupId>
    <artifactId>xatkit-nlu-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> Right now, you need to use the x

Finally, add the following mandatory [properties](https://github.com/xatkit-bot-platform/xatkit/wiki/Xatkit-Options)
to your bot configuration file (replace the values with the proper values for your case)

```properties
xatkit.intent.provider = com.xatkit.core.recognition.nluserver.NLUServerIntentRecognitionProvider
xatkit.nluserver.botname = MyBotName
xatkit.nluserver.url = http://127.0.0.1:8000
xatkit.nluserver.force_overwrite = true
```

Note that you can use static constants to set these values if you are working with a configuration object (and not a file):
```java
Configuration botConfiguration = new BaseConfiguration();
botConfiguration.addProperty(IntentRecognitionProviderFactory.INTENT_PROVIDER_KEY,
       NLUServerConfiguration.NLUSERVER_INTENT_PROVIDER);
botConfiguration.addProperty(NLUServerConfiguration.BOT_NAME, "SimpleBot");
botConfiguration.addProperty(NLUServerConfiguration.URL, "http://127.0.0.1:8000");
botConfiguration.addProperty(NLUServerConfiguration.FORCE_OVERWRITE, true);
```

The semantics of these mandatory options are:
- provider: Tells the bot to use this IntentRecognitionProvider
- BotName, used to identify the bot in the potentially several bots co-existing in the Xatkit NLU Engine
- URL where to server is deployed
- Force_overwrite: whether the bot should overwrite an existing bot with the same name deployed in the server. If 
  false and a bot exists, the deployment of the *repeated* bot will trigger an error


# Additional configuration options
All the
[NLU Configuration options](https://github.com/xatkit-bot-platform/xatkit-nlu-client#additional-configuration-options)
available in the server description are mirrored in the client (and passed on to the server during the training), 
with a similar name.

Additionally, these are the client-specific configuration options you can set in your bot.

| Key                   | Values  | Description                                             | Constraint                |
|-----------------------|---------|---------------------------------------------------------|---------------------------|
| `confidenceThreshold` | float   | Minimum confidence level to accept an intent is a match | Optional (default `0.3`)  |

# Example

This repo includes a `SampleBotTest.java` with a minimal bot with the mandatory configuration options as an example
