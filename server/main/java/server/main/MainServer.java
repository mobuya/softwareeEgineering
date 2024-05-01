package server.main;

import java.util.Collections;

import javax.validation.Validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/*
 * Should already be configured correctly for all use cases, i.e., you will most likely not 
 * need to change this class. If you move the ServerEndpoints to a different package, you need to use 
 * @ComponentScan(basePackages = "Complete.Path.Identifier.For.Your.FIRST.Package.With.Endpoints", "Complete.Path.Identifier.For.Your.SECOND.Package.With.Endpoints")
 * here as an additional annotation where the part in the double quotes are (one or more) 
 * packages (separated by commas) that SPRING should search for beans, components/services, repositories, 
 * and also, relevant for us, controllers holding endpoints 
 */
@SpringBootApplication
@ComponentScan
@Configuration
public class MainServer {

	/*
	 * Note, the server was already configured by us to run on port 18235. You can
	 * adapt this, e.g., in the application.properties when adding it to the build
	 * path or by using the code given below. Note, if you would like to run/debug
	 * the server multiple times in a row, you will need to close all old running
	 * versions as otherwise the port would already be in use. You
	 * can do this in the debug screen, select the server instance and press the red
	 * stop button in the toolbar.
	 */
	// the test client assumes 18235 as it's default port
	private static final int DEFAULT_PORT = 18235;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MainServer.class);

		// sets the port programmatically. One could also set it based on the .properties file
		app.setDefaultProperties(Collections.singletonMap("server.port", DEFAULT_PORT));
		app.run(args);
	}
}
