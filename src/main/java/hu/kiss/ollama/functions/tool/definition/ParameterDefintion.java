package hu.kiss.ollama.functions.tool.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.kiss.ollama.functions.tool.Argument;
import java.lang.reflect.Parameter;

/**
 *
 * @author Kiss Attila
 */
public class ParameterDefintion {

    @JsonIgnore
    private String name;
    @JsonIgnore
    private String description;
    private String type;

    public ParameterDefintion(Parameter p){
        assert p.isAnnotationPresent(Argument.class);
        var a = p.getAnnotation(Argument.class);
        this.name = a.name();
        this.description = a.definition();
        this.type = p.getType().getSimpleName();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String definition) {
        this.description = definition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
