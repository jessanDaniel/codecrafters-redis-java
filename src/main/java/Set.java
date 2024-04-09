public class Set implements Command {
    @Override
    public String execute(String input) {
        return "+" + input + "\r\n";
    }

}
