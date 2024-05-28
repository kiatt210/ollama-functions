package hu.kiss.ollama.functions.tool.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.kiss.ollama.functions.tool.Argument;
import hu.kiss.ollama.functions.tool.Calls;
import hu.kiss.ollama.functions.tool.Function;
import hu.kiss.ollama.functions.tool.FunctionCall;
import hu.kiss.ollama.functions.tool.Tool;
import hu.kiss.ollama.functions.tool.definition.FunctionDefinition;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.json.JSONObject;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

/**
 *
 * @author Kiss Attila
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final OllamaChatClient client;

    private final Map<String, Object> toolMethods = new HashMap<>();
    private final Map<String, Object> parameters = new HashMap<>();
    private final Tool[] tools;
    private final PromptTemplate inputPrompt;
    private final PromptTemplate responsePrompt;
    private final ObjectMapper mapper;
    
    private String message;
    
    public ChatService(OllamaChatClient client,@Value("classpath:request.st") Resource systemPrompt,@Value("classpath:user.st") Resource userPrompt,ObjectMapper mapper,Tool... tools) {
        log.debug("Init...");
        this.client = client;
        this.tools = tools;
        this.mapper = mapper;
        
        inputPrompt = new PromptTemplate(systemPrompt);
        responsePrompt = new PromptTemplate(userPrompt);
        
        parameters.put("tools", getToolDefinition());
    }

    public String chat(String message) {

        log.debug("Question sent");
        this.message = message;
        log.info("User> "+message);
        
        parameters.put("time", LocalDateTime.now());
        parameters.put("input", message);
        Prompt p = inputPrompt.create(parameters);
        
        log.info("Prompt> "+p.getContents());
        
        var response = client.call(p);
        
        log.info("LLM> " + response.getResult().getOutput().getContent());

        if(isFunctionCall(response)){
            return handleFunctionCall(response);
        }
        
        return response.getResult().getOutput().getContent();
    }
    
    protected Boolean isFunctionCall(ChatResponse response){
        var responseStr = response.getResult().getOutput().getContent().trim();
        
        return responseStr.startsWith("{") && responseStr.endsWith("}");
    }
    
    private String handleFunctionCall(ChatResponse response){
        
        var json = response.getResult().getOutput().getContent();

        try {
            var calls = mapper.readValue(json, Calls.class);
            log.debug(calls.toString());
            return functionCall(calls.getCalls().get(0));
        } catch (JsonProcessingException ex) {
            log.error("Json parse error:", ex);
            return "";
        }
    }

    public String getToolDefinition() {

        List<FunctionDefinition> definitions = new ArrayList<>();
        
        for (Object tool : tools) {
            for (Method m : tool.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(Function.class)) {
                    var t = m.getAnnotation(Function.class);
                    toolMethods.put(t.name(), tool);
                    definitions.add(new FunctionDefinition(m));
                }
            }
        }

        log.debug(definitions.toString());
        return definitions.toString();
    }

    private String functionCall(FunctionCall call) {
        var toolObject = toolMethods.get(call.getName());
        Method method = null;
        for (Method m : toolObject.getClass().getMethods()) {
            if (m.isAnnotationPresent(Function.class) && ((Function) m.getAnnotation(Function.class)).name().equals(call.getName())) {
                method = m;
            }
        }

        assert method != null;
        Object[] params = new Object[method.getParameterCount()];
        var paramIx = 0;
        for (int i = 0; i < method.getParameterCount(); i++) {
            var p = method.getParameters()[i];
            if (p.isAnnotationPresent(Argument.class)) {
                Argument arg = p.getAnnotation(Argument.class);
                Object param = call.getArguments().get(arg.name());
                params[paramIx] = param;
                paramIx++;
            }
        }

        try {
            Object response = method.invoke(toolObject, params);
            log.info("Call " + method.getName() + " -> " + response.toString());
            
            JSONObject callResponse = new JSONObject();
            JSONObject responseObj = new JSONObject();
            responseObj.put("response", response);
            callResponse.put(call.getName(), responseObj);

            Map<String, Object> functionParameters = new HashMap<>();
            functionParameters.put("result", callResponse.toString());
            functionParameters.put("input", message);
            
            Prompt p = responsePrompt.create(functionParameters);
            log.info("Function call result> "+p.getContents());
            var llmResponse = client.call(p);
            log.info("LLM> "+llmResponse.getResult().getOutput().getContent());
            return llmResponse.getResult().getOutput().getContent();
        } catch (IllegalAccessException | InvocationTargetException ex) {
            log.error("Function call error:",ex);
        }
        
        return "";
    }

}
