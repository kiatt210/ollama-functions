package hu.kiss.ollama.functions.tool.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.kiss.ollama.functions.tool.Argument;
import hu.kiss.ollama.functions.tool.Function;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Function definition holder.
 * Create a json from it and pass to the llm.
 * @author Kiss Attila
 */
public class FunctionDefinition {

    ObjectMapper mapper = new ObjectMapper();
    
    private String name;
    private String returns;
    private String description;
    private List<ParameterDefintion> arguments = new ArrayList<>();

    public FunctionDefinition(Method m) {
        assert m.isAnnotationPresent(Function.class);
        var t = m.getAnnotation(Function.class);
        this.name = t.name();
        this.description = t.definition();
        this.returns = m.getReturnType().getSimpleName();
        createParameterDefinitions(m.getParameters());
    }
    
    private void createParameterDefinitions(Parameter[] parameters){
        for(Parameter p : parameters){
            if(p.isAnnotationPresent(Argument.class)){
                this.arguments.add(new ParameterDefintion(p));
            }
        }
    }

    @Override
    public String toString() {
        
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(FunctionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            return "EXCEPTION:"+ex.getMessage();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturns() {
        return returns;
    }

    public void setReturns(String returns) {
        this.returns = returns;
    }

    public String getDescription() {
        StringBuilder descSb = new StringBuilder();
        descSb.append(description);
        if(!arguments.isEmpty()) descSb.append("\n\nArgs:");
        arguments.forEach(a -> {
            descSb.append("\n");
            descSb.append(a.getName());
            descSb.append(" (");
            descSb.append(a.getType());
            descSb.append("): ");
            descSb.append(a.getDescription());
            descSb.append("\n");
                });

        return descSb.toString();
    }

    public void setDescription(String definition) {
        this.description = definition;
    }

    public Map<String,ParameterDefintion> getArguments(){
        return arguments.stream().collect(Collectors.toMap(a -> a.getName(), a->a));
    }

    public void setArguments(List<ParameterDefintion> parametersDefinitions) {
        this.arguments = parametersDefinitions;
    }
    
    
    
}
