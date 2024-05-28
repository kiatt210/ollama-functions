package hu.kiss.ollama.functions.tool;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kiss Attila
 */
public class Calls {

    @JsonAlias("tool_calls")
    private List<FunctionCall> calls = new ArrayList<>();

    public List<FunctionCall> getCalls() {
        return calls;
    }

    public void setCalls(List<FunctionCall> calls) {
        this.calls = calls;
    }
    
    
}
