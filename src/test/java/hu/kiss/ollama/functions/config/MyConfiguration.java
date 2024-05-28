package hu.kiss.ollama.functions.config;

import hu.kiss.ollama.functions.tool.Tool;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Kiss Attila
 */
@TestConfiguration
public class MyConfiguration {

  @Bean  
  public Tool myTool(){
      return new TestTool();
  }
}
