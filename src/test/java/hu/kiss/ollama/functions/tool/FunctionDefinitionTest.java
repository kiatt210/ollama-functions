package hu.kiss.ollama.functions.tool;

import hu.kiss.ollama.functions.tool.definition.FunctionDefinition;
import java.lang.reflect.Method;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Kiss Attila
 */
public class FunctionDefinitionTest {

    @Function(name = "get_task_count",definition = "Use this to get the number of tasks in redmine\n\nReturns:\n    str: The number of tasks.")
    public Integer getTaskCount(){
        return 0;
    }
    
    @Function(name="add_task",definition = "Use this to create a new task in redmine\n\nReturns:\nstr: The id of the new task")
    public String addTask(@Argument(name = "name",definition="The name of the task.") String name){
        return "";
    }

    @Test
    public void testNonArgumentJsonDefinitionGeneration() throws NoSuchMethodException{
        JSONObject description = new JSONObject();
        
        description.put("name", "get_task_count");
        description.put("description", "Use this to get the number of tasks in redmine\n\nReturns:\n    str: The number of tasks.");
        description.put("returns", "Integer");
        description.put("arguments", new JSONObject());

        Method m = this.getClass().getDeclaredMethod("getTaskCount",null);
        assertNotNull(m);
        FunctionDefinition def = new FunctionDefinition(m);
        assertNotNull(def);
        assertEquals("Use this to get the number of tasks in redmine\n\nReturns:\n    str: The number of tasks.",def.getDescription());
        assertEquals("get_task_count", def.getName());
        assertEquals("Integer", def.getReturns());
        assertEquals(description.toString(), new JSONObject(def.toString()).toString());
    }

    @Test
    public void testJsonDefinitionGeneration() throws NoSuchMethodException{
        JSONObject description = new JSONObject();
        
        description.put("name", "add_task");
        description.put("description", "Use this to create a new task in redmine\n\nReturns:\nstr: The id of the new task\n\nArgs:\nname (String): The name of the task.\n");
        description.put("returns", "String");
        
        var arguments = new JSONObject();
        var name = new JSONObject();
        name.put("type", "String");
        arguments.put("name", name);
        description.put("arguments", arguments);
        
        Method m = this.getClass().getDeclaredMethod("addTask",String.class);
        assertNotNull(m);
        FunctionDefinition def = new FunctionDefinition(m);
        assertNotNull(def);
        assertEquals("Use this to create a new task in redmine\n\nReturns:\nstr: The id of the new task\n\nArgs:\nname (String): The name of the task.\n",def.getDescription());
        assertEquals("add_task", def.getName());
        assertEquals("String", def.getReturns());
        assertEquals(description.toString(), new JSONObject(def.toString()).toString());
        
        m = this.getClass().getDeclaredMethod("getTaskCount",null);
        System.out.println(new FunctionDefinition(m));
        m = this.getClass().getDeclaredMethod("addTask",String.class);
        System.out.println(new FunctionDefinition(m));
    }
    
}
