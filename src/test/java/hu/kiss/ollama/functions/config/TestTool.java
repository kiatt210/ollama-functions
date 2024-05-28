package hu.kiss.ollama.functions.config;

import hu.kiss.ollama.functions.tool.Argument;
import hu.kiss.ollama.functions.tool.Function;
import hu.kiss.ollama.functions.tool.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kiss Attila
 */
public class TestTool implements Tool {

    private List<String> tasks = new ArrayList<>();
    
    
    @Function(name = "get_task_count", definition = "Can return the number of tasks in redmine")
    public Integer getTaskCount() {
        return tasks.size();
    }

    @Function(name = "create_task", definition = "Can create a new task to the redmine. Returns the id of the new task.")
    public String addTask(@Argument(name = "task_name", definition = "Name of the new task") String task) {
        tasks.add(task);
        return tasks.size()+"";
    }
}
