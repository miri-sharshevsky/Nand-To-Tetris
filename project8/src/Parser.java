import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private Scanner scanner;
    private String line;

    public Parser(String path) throws FileNotFoundException {
        scanner = new Scanner(new File(path));
    }
    public boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }
    public void advance() {

        if (hasMoreCommands()) {
            String tempLine = scanner.nextLine().trim().replaceAll("\\s+"," ");//.replaceAll(" ", "");
            int index = tempLine.indexOf('/');
            if (index == 0) {
                tempLine = null;
            }
            if (index > 0 && index != tempLine.length() && tempLine.charAt(index + 1) == '/') {
                if (!tempLine.equals("")) {
                    line = tempLine.substring(0, index);
                    return;
                }
            }
            line = tempLine;
        }
    }

    public String commandType(){

        if (line == null || line.equals("")) {
            return null;
        }
        if(line.startsWith("push")){
            return "C_PUSH";
        }
        if(line.startsWith("pop")){
            return "C_POP";
        }
        if(line.startsWith("label")){
            return "C_LABEL";
        }
        if(line.startsWith("function")){
            return "C_FUNCTION";
        }
        if(line.equals("return")){
            return "C_RETURN";
        }
        if(line.startsWith("call")){
            return "C_CALL";
        }
        if(line.startsWith("if")){
            return "C_IF";
        }
        if(line.startsWith("goto")){
            return "C_GOTO";
        }else {
            return "C_ARITHMETIC";
        }
    }
    public String arg1(){
        String commandType = commandType();
        if(!commandType.equals("C_RETURN")){
            if(commandType.equals("C_ARITHMETIC")){
                line = line.trim().replaceAll(" ","");
                return line;
            }else {
                return line.split(" ")[1];
            }
        }
        return null;
    }

    public String arg2(){
        String commandType = commandType();
        if(commandType.equals("C_POP")|| commandType.equals("C_PUSH") || commandType.equals("C_FUNCTION")||commandType.equals("C_CALL")){
            return line.split(" ")[2];
        }
        return null;
    }
}
