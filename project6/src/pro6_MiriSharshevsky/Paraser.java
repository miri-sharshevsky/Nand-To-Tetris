import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Paraser {
    private String path;
    private String line;
    private Scanner scanner;

    public Paraser(String path) throws FileNotFoundException {
        this.path = path;
        scanner = new Scanner(new File(path));
    }

    public boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    public void advanced() {

        if (hasMoreCommands()) {
            String tempLine = scanner.nextLine().replaceAll(" ", "");
            int index = tempLine.indexOf('/');
            if (index == 0) {
                tempLine = null;
            }
            if (index > 0 && index != tempLine.length() && tempLine.charAt(index + 1) == '/') {
                if (tempLine != "") {
                    line = tempLine.substring(0, index);
                    return;
                }
            }
            line = tempLine;
        }
    }

    public String commandType() {
        if (line == null || line.equals("")) {
            return null;
        }
        if (line.charAt(0) == '@') {
            return "A_COMMAND";
        }
        if (line.charAt(0) == '(') {
            return "L_COMMAND";
        }
        return "C_COMMAND";
    }

    public String symbol() {
        if (commandType().equals("A_COMMAND") || commandType().equals("L_COMMAND")) {
            String symbol;
            symbol = line.substring(1);
            if (line.charAt(line.length() - 1) == ')') {
                symbol = symbol.substring(0, line.length() - 2);
            }
            return symbol;

        }
        return null;
    }

    public String dest() {
        if (commandType().equals("C_COMMAND")) {
            if (line.contains("=")) {
                int index = line.indexOf('=');
                return line.substring(0, index);
            }
        }
        return null;
    }

    public String comp() {
        if (commandType().equals("C_COMMAND")) {
            int index1 = line.indexOf('=') + 1;
            int index2 = line.indexOf(';');
            if (index2 == -1) {
                index2 = line.length();
            }
            return line.substring(index1, index2);
        }
        return null;
    }

    public String jump() {
        if (commandType().equals("C_COMMAND")) {
            int index = line.indexOf(';') + 1;
            return line.substring(index);
        }
        return null;
    }


//    private String readCode() {
//        try {
//            BufferedReader bufferreader = new BufferedReader(new FileReader(path));
//            while ((line = bufferreader.readLine()) != null){
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
