# JSON Persistence

This Gareth module can be used to stored the Gareth application state into a JSON file.

## How to use

For using the JSON persistence module the following steps must be taken:


```xml
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-core</artifactId>
    <version>0.8.2</version>
</dependency>
<!-- Add this module -->
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-json-persistence</artifactId>
    <version>0.8.2</version>
</dependency>
```

After adding the dependency, the alternative persistence can then be configured within the Gareth ExperimentEngine
builder.

```java
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.core.ExperimentEngine;

public class GarethContext {

    public static void main(final String[] args) throws Exception {
        final RestServiceFactory restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory
        final ExperimentEngineConfig config = new ExperimentEngineConfigImpl.Builder().build();
        final ExperimentEngine experimentEngine = new ExperimentEngineImpl
                        .Builder(experimentEngineConfig)
                        .setExperimentEnginePersistence(new JsonExperimentEnginePersistence.Builder().build()) // Load alternative persistence here
                        .build();
        engine.start();
    }
}
```