package hu.kiss.ollama.functions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class OllamaFunctionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OllamaFunctionsApplication.class, args);
    }

    @Bean
    public RestClient.Builder builder() {
        return RestClient.builder();
    }
}
