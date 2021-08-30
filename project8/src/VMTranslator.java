import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

class VMTranslator {
    private static CodeWriter codeWriter;

    public static void main(String[] args) throws Exception {
        //if path  sent in args
        String path;
        if (args.length != 0) {
            path = args[0];
        } else {
            //else get path from user
            System.out.println("insert file path\n");
            Scanner sc = new Scanner(System.in);
            path = sc.nextLine();
        }
        //get all file.vm in dir
        List<String> allFilesInDir = findFiles(path);
        codeWriter = new CodeWriter(path.replace(".vm", ".asm"));
        //if there is more then one file
        if (allFilesInDir.size() >= 2) {
            codeWriter.writeInit();
        }
        //translate every file in dir
        for (String fileNowPath : allFilesInDir) {
            Parser parser = new Parser(fileNowPath);
            codeWriter.setFileName(fileNowPath);
            translateFile(parser);
        }
        codeWriter.close();
    }


    public static void translateFile(Parser parser) {
        //parse file
        while (parser.hasMoreCommands()) {
            //get the next command clear
            parser.advance();
            String commandType = parser.commandType();
            if (commandType == null) {
                continue;
            }
            //write the assembly code that appropriate to the command type
            switch (commandType) {
                case "C_ARITHMETIC":
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                case "C_PUSH":
                case "C_POP":
                    codeWriter.writePushPop(commandType, parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case "C_LABEL":
                    codeWriter.writeLabel(parser.arg1());
                    break;
                case "C_FUNCTION":
                    codeWriter.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case "C_RETURN":
                    codeWriter.writeReturn();
                    break;
                case "C_CALL":
                    codeWriter.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case "C_IF":
                    codeWriter.writeIf(parser.arg1());
                    break;
                case "C_GOTO":
                    codeWriter.writeGoto(parser.arg1());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * get all the vm files in the directory
     *
     * @param fileName the path of the directory to find there
     * @return list that containing all the vm fiels
     */
    public static List<String> findFiles(String fileName) {
        File dir = new File(fileName);
        List<String> allFiles = new ArrayList<String>();
        if (dir.isDirectory())
            findAllFiles(Objects.requireNonNull(dir.listFiles()), allFiles);
        else allFiles.add(fileName);
        return allFiles;
    }

    /**
     * Auxiliary function - check if the file is vm type- add it to the all files list
     *
     * @param listFilesInDir array of the all files in the directory
     * @param allFiles       all vm files in the directory
     */
    public static void findAllFiles(File[] listFilesInDir, List<String> allFiles) {
        for (File file : listFilesInDir) {
            if (file.isDirectory()) {
                findAllFiles(Objects.requireNonNull(file.listFiles()), allFiles); // Calls same method again.
            } else {
                String path = file.getAbsolutePath();
                if (path.contains(".vm")) {
                    allFiles.add(file.getAbsolutePath());
                }
            }
        }
    }

}