package by.art.taskprocessingsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Processing System API",
                version = "1.0",
                description = "API for task management and processing"
        )
)
public class SwaggerConfiguration {
}
