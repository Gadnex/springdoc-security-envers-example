package net.binarypaper.example.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "${application.name}",
        description = "${application.description}",
        version = "${application.version}",
        contact = @Contact(
                name = "William Gadney",
                email = "gadnex@gmail.com"
        ),
        license = @License(
                name = "APACHE LICENSE, VERSION 2.0",
                url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
))
public class SpringdocConfiguration {

}
