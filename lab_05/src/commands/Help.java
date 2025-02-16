package commands;

/**
 * The Help class displays a list of available commands.
 */
public class Help {
    /**
     * Displays all available commands and their descriptions.
     */
    public Help() {
        System.out.println("Available commands:");
        System.out.println("help -> List available commands");
        System.out.println("info -> Display collection information");
        System.out.println("add {element} -> Add a new element to the collection");
        System.out.println("update {id} {element} -> Update an element by its ID");
        System.out.println("remove_by_id {id} -> Remove an element by its ID");
        System.out.println("clear -> Clear the collection");
        System.out.println("save -> Save the collection to a file");
        System.out.println("execute_script {filename} -> Execute commands from a script");
        System.out.println("exit -> Exit the program");
        System.out.println("remove_first -> Remove the first element from the collection");
        System.out.println("reorder -> Reverse the order of the collection");
        System.out.println("sort -> Sort the collection");
        System.out.println("min_by_id -> Display the element with the smallest ID");
        System.out.println("filter_by_transport {transport} -> Display elements with the given transport type");
        System.out.println("filter_contains_name {name} -> Display elements containing the given name");
    }
}
