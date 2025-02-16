package commands;

public class Help {
    public Help() {
        System.out.println("Hi! I'm an help command! List of commands: ");
        System.out.println("help -> List of available commands");
        System.out.println("info -> information about collection(type, date of initialization, count of elements etc.");
        System.out.println("add {element} -> add a new element to the collection");
        System.out.println("update id {element} -> update value by written id");
        System.out.println("remove_by_id {id} -> remove a element from the collection by his id");
        System.out.println("clear -> clear the collection");
        System.out.println("save -> save the collection to the file");
        System.out.println("execute_script {filename}-> execute and start script from the file");
        System.out.println("exit -> exit the program");
        System.out.println("remove_first -> remove the first element from the collection");
        System.out.println("reorder -> reorder the collection");
        System.out.println("sort -> sort the collection");
        System.out.println("min_by_id -> print element from the collection with minimal id");
        System.out.println("filter_by_transport {transport} -> print elements, which transport value equals input transport");
        System.out.println("filter_contains_name {name} -> print elements, which contains the name of the element");
    }
}