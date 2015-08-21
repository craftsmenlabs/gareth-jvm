# Gareth

Gareth is platform that allows you to make business goal validation part of your development process.

## Running Gareth

Gareth can be run in two different manners, can be run with or without a REST interface.

### Running Gareth (without REST interface)

Running Gareth without a REST interface can be done by creating a maven project that depends on the following maven
dependency:

```xml
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-core</artifactId>
    <version>0.3.0</version>
</dependency>
```

Create a java application that starts the Gareth framework:

```java
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.examples.definition.SampleDefinition;

public class ExampleApplication {
    public static void main(final String[] args) {
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(SampleDefinition.class) // Load the classes with your definitions
                .addInputStreams(ExampleApplication.class.getClass().getResourceAsStream("/experiments/businessgoal-01.experiment")) // Load the inputstreams with your experiments
                .setIgnoreInvocationExceptions(true)
                .build();
        final ExperimentEngine experimentEngine = new ExperimentEngineImpl
                .Builder(experimentEngineConfig)
                .build();
        experimentEngine.start();
    }
}
```

For building the standalone application, you can use the maven-shade-plugin to create a single jar containing all the
necessary classes. You can add this your project pom.xml.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.4.1</version>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>org.craftsmenlabs.gareth.examples.ExampleApplication</mainClass>
                    </transformer>
                </transformers>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

After doing a ```maven clean package``` you now can run the Gareth platform with your own definitions and experiments.

```shell
java -jar /path/to/project.jar
```

### Running Gareth (with REST interface)

Running Gareth with a REST interface can be done by creating a maven project that depends on the following maven
dependency:

```xml
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-core</artifactId>
    <version>0.3.0</version>
</dependency>
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-rest</artifactId>
    <version>0.3.0</version>
</dependency>
```

Create a java application that starts the Gareth framework:

```java
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethContext {

    public static void main(final String[] args) throws Exception {
        final RestServiceFactory restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory
        final ExperimentEngineConfig config = new ExperimentEngineConfigImpl.Builder().build();
        final ExperimentEngine engine = new ExperimentEngineImpl.Builder(config).setRestServiceFactory(restServiceFactory).build(); // And just include it in the engine
        engine.start();
    }
}
```

For building the standalone application, you can use the maven-shade-plugin to create a single jar containing all the
necessary classes. You can add this your project pom.xml.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.4.1</version>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>org.craftsmenlabs.gareth.examples.ExampleApplication</mainClass>
                    </transformer>
                </transformers>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

After doing a ```maven clean package``` you now can run the Gareth platform with your own definitions and experiments.

```shell
java -jar /path/to/project.jar
```
## Contribute

You can contribute to this repository the by following these steps.

- Fork this repository
- Create a branch
- Do your coding
- Create a pull request to integrate the branch

## Planned

The following functionality is planned for next releases:

- [ ] Add experiment examples
- [ ] Re-run experiments
- [x] Build examples
- [x] REST interface
- [ ] Maintain state after restart
- [ ] Store values thru experiments
- [x] Replace AKKA with lightweight scheduler
- [ ] Add additional unit-tests