public class Expired implements Command {
    @Override
    public String execute(String input) {
        return "$-1\r\n";
    }

}
