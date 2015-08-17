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
    <version>0.2.0</version>
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
## Contribute

You can contribute to this repository the by following these steps.

- Fork this repository
- Create a branch
- Do your coding
- Create a pull request to integrate the branch

## Planned

The following functionality is planned for next releases:

- [] Build examples
- [] REST interface
- [] Maintain state after restart
- [] Store values thru experiments
- [x] Replace AKKA with lightweight scheduler