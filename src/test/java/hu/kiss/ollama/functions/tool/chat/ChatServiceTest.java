package hu.kiss.ollama.functions.tool.chat;

import hu.kiss.ollama.functions.config.MyConfiguration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.Captor;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Kiss Attila
 */
@SpringBootTest
@Import(MyConfiguration.class)
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;
    
    @MockBean
    private OllamaChatClient ollamaChatClient;
    
    @Captor
    ArgumentCaptor<Prompt> valueCaptor;
    
    @Test
    public void testService(){
        assertNotNull(chatService);
    }
    
    @Test
    public void testFunctionCall(){
        
        ChatResponse response = new ChatResponse(List.of(new Generation("""
{
"tool_calls": [
    {
        "name": "add_task",
        "arguments": {
            "task_name": "Create more tests"
        }
    }
]
}
""")));
     assertTrue(chatService.isFunctionCall(response));
        
    }
    
    @Test
    public void testNormalCall(){
        
        ChatResponse response = new ChatResponse(List.of(new Generation("""
As of 2024-05-27 13:38:10.718447, we currently have **23** tasks in Redmine!
""")));
     assertFalse(chatService.isFunctionCall(response));
        
    }
    
    @Test
    public void testPromptContainsTools(){
        
        when(ollamaChatClient.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation("Thanks I'm fine"))));
        chatService.chat("How are you?");
        
        verify(ollamaChatClient).call(argThat(new ArgumentMatcher<Prompt>() {
            @Override
            public boolean matches(Prompt t) {
                return t.getContents().contains("add_task");
            }
        }));
        
    }
    
    @Test
    public void testHandleFunctionCall(){

        when(ollamaChatClient.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation("""
{
"calls": [
    {
        "name": "get_task_count",
        "arguments": {}
    }
]
}
"""))));
        
        var question = "How many task do we have?";
        chatService.chat(question);
        
        verify(ollamaChatClient,times(2)).call(valueCaptor.capture());
        assertTrue(valueCaptor.getAllValues().get(1).getContents().contains("""
{"response":3}
"""));
        assertTrue(valueCaptor.getAllValues().get(1).getContents().contains(question));

    }
    
    @Test
    public void testHandleFunctionCallWithArguments(){

        when(ollamaChatClient.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation("""
{
"calls": [
    {
        "name": "add_task",
        "arguments": {
            "task_name":"Test"
         }
    }
]
}
"""))));
        
        var question = "How many task do we have?";
        chatService.chat(question);
        
        verify(ollamaChatClient,times(2)).call(valueCaptor.capture());
        assertTrue(valueCaptor.getAllValues().get(1).getContents().contains("""
{"response":"1"}
"""));
        assertTrue(valueCaptor.getAllValues().get(1).getContents().contains(question));

    }
    
}
