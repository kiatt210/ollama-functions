package hu.kiss.ollama.functions.tool.chat;

import hu.kiss.ollama.functions.config.MyConfiguration;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Kiss Attila
 */
@SpringBootTest
@Import(MyConfiguration.class)
public class LLama3Test {
    
    @Autowired
    ChatService service;
    
    @Test
    public void testWithRealModel(){
        assertNotNull(service);
        String response = service.chat("How many tasks do we have?");
        assertTrue(response.contains("0"));
        response = service.chat("Create a new task with name \"Write more tests\" ");
        assertTrue(response.contains("1"));
        response = service.chat("How many tasks do we have?");
        assertTrue(response.contains("1"));
    }
    
}
