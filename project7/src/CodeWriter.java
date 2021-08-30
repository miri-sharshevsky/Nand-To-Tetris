import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * Generates assembly code from the parsed VM command:
 */
public class CodeWriter {
    private PrintWriter writer;
    private int equals = 1;
    private String fileName;

    /**
     * constructor - open the file
     * @param path - path for the assembly file
     * @throws Exception- if file doesn't open
     */
    public CodeWriter(String path) throws Exception {
        File file = new File(path);
        writer = new PrintWriter(new FileWriter(file), true);
        fileName = file.getName();
        fileName.substring(0, fileName.length()-4);
    }

    /**
     * Write to the output file the assembly code that implements the arithmetic command
     * @param command - the command to translate
     */
    public void writeArithmetic(String command) {
        String line;
        if (command.matches("add|sub|and|or")) {
            switch (command) {
                case "add":
                    line = "D=D+M";
                    break;
                case "sub":
                    line = "D=M-D";
                    break;
                case "and":
                    line = "D=D&M";
                    break;
                case "or":
                    line = "D=D|M";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
            writer.append("@SP\nM=M-1\nA=M\nD=M\nA=A-1\n");
            writer.append(line).append("\n");
            writer.append("M=D\nD=A+1\n@SP\nM=D\n");
        } else if (command.matches("neg|not")) {
            switch (command) {
                case "neg":
                    line = "M=-M";
                    break;
                case "not":
                    line = "M=!M";

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
            writer.append("@SP\nM=M-1\nA=M\n");
            writer.append(line).append("\n");
            writer.append("D=A+1\n@SP\nM=D\n");
        } else {
            switch (command) {
                case "eq":
                    line = "D;JEQ";
                    break;
                case "lt":
                    line = "D;JLT";
                    break;
                case "gt":
                    line = "D;JGT";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }



            writer.append("@SP\nM=M-1\nA=M\nD=M\nA=A-1\nD=M-D\n");
            writer.append("@EQ").append(String.valueOf(equals)).append("\n");
            writer.append(line).append("\n");
            writer.append("@0\nD=-A\n@SP\nM=M-1\nA=M\nM=D\n@SP\nM=M+1\n");
            writer.append("@EqToDo").append(String.valueOf(equals)).append("\n");
            writer.append("0;JMP\n(EQ").append(String.valueOf(equals)).append(")\n");
            writer.append("@SP\nM=M-1\nA=M\nM=-1\n@SP\nM=M+1\n(EqToDo").append(String.valueOf(equals)).append(")\n");

            equals++;
        }
    }

    /**
     * write to the output file the assembly code that implements the arithmetic command
     * @param command - command to translate
     * @param segment - segment to push/pop to/from
     * @param index - index to push/pop from/to
     */
    public void writePushPop(String command, String segment, int index) {
        if (segment.equals("constant")) {
            writer.append("@").append(String.valueOf(index)).append("\nD=A\n");
        } else if (segment.matches("this|that|local|argument")) {
            String key = "";
            switch (segment) {
                case "local":
                    key = "LCL";
                    break;
                case "this":
                    key = "THIS";
                    break;
                case "that":
                    key = "THAT";
                    break;
                case "argument":
                    key = "ARG";
                    break;
                default:
                    break;
            }
            writer.append("@").append(key).append("\nD=M\n@").append(String.valueOf(index)).append("\nD=D+A\n");
            if(command.equals("C_PUSH") ){
                writer.append("A=D\nD=M\n");
            }
        } else if (segment.equals("temp")) {
            writer.append("@5\nD=A\n@").append(String.valueOf(index)).append("\nD=D+A\n");
            if(command.equals("C_PUSH")){
                writer.append("A=D\nD=M\n");
            }
        } else if (segment.equals("pointer")) {
            writer.append("@3\nD=A\n@").append(String.valueOf(index)).append("\nD=D+A\n");
            if(command.equals("C_PUSH")){
                writer.append("A=D\nD=M\n");
            }
        } else {
            writer.append("@").append(fileName).append(".").append(String.valueOf(index)).append("\nD=A\n");
            if(command.equals("C_PUSH")){
                writer.append("A=D\nD=M\n");
            }
        }
        if (command.equals("C_POP")) {
            writer.append("@R13\nM=D\n@SP\nA=M-1\nD=M\n@SP\nM=M-1\n@R13\nA=M\nM=D\n");
        } else if (command.equals("C_PUSH")) {
            writer.append("@SP\nA=M\nM=D\nD=A+1\n@SP\nM=D\n");
        }
    }

    /**
     * finish translate and close the file
     */
    public void close(){
        writer.append("(AND)\n@AND\n0;JMP");
        writer.close();
    }
}
