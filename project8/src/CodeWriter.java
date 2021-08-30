import java.io.*;


public class CodeWriter {
    private PrintWriter writer;
    private int numLabel = 1;
    private String fileName;
    private int callCounter = 0;

    /**
     * constructor - open file
     * @param path - path for file to write the assembly code
     * @throws Exception if file cannot open
     */
    public CodeWriter(String path) throws Exception {
        File file = new File(path);

        if (!path.contains(".asm")) {
            fileName = file.getName();
            path = path + "\\" + fileName + ".asm";
            file = new File(path);
        }
        writer = new PrintWriter(new FileWriter(file), true);
        fileName = "init";
    }

    /**
     *set the file name
     * @param path
     */
    public void setFileName(String path) {
        File file = new File(path);
        fileName = file.getName();
        this.fileName = fileName.substring(0, fileName.length() - 3);
    }

    /**
     * writes code which performs arithmetic command
     * @param command
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
            //writes code which calculates action on RAM[SP-1], RAM[SP-2] to RAM[SP-2] and to D
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
            //writes code which performs unary arithmetic action, on RAM[SP]
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
                    line = null;
            }
            //writes code which performs binary condition arithmetic action
            writer.append("@SP\nM=M-1\nA=M\nD=M\nA=A-1\nD=M-D\n");
            writer.append("@COMP").append(String.valueOf(numLabel)).append("\n");
            writer.append(line).append("\n");
            writer.append("@0\nD=-A\n@SP\nM=M-1\nA=M\nM=D\n@SP\nM=M+1\n");
            writer.append("@DONE_COMP").append(String.valueOf(numLabel)).append("\n");
            writer.append("0;JMP\n(COMP").append(String.valueOf(numLabel)).append(")\n");
            writer.append("@SP\nM=M-1\nA=M\nM=-1\n@SP\nM=M+1\n(DONE_COMP").append(String.valueOf(numLabel)).append(")\n");

            numLabel++;
        }
    }

    /**
     * writes code which performs push or pop command
     * @param command PUSH or POP
     * @param segment - this or that or local or argument
     * @param index - index to push/pop in the segment
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
            if (command.equals("C_PUSH")) {
                writer.append("A=D\nD=M\n");
            }
        } else if (segment.equals("temp")) {
            writer.append("@5\nD=A\n@").append(String.valueOf(index)).append("\nD=D+A\n");
            if (command.equals("C_PUSH")) {
                writer.append("A=D\nD=M\n");
            }
        } else if (segment.equals("pointer")) {
            writer.append("@3\nD=A\n@").append(String.valueOf(index)).append("\nD=D+A\n");
            if (command.equals("C_PUSH")) {
                writer.append("A=D\nD=M\n");
            }
        } else {
            writer.append("@").append(fileName).append(".").append(String.valueOf(index)).append("\nD=A\n");
            if (command.equals("C_PUSH")) {
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
     * writes code which inits the RAM
     */
    public void writeInit() {
        writer.append("@256\nD=A\n@SP\nM=D\n");
        writeCall("Sys.init", 0);
    }

    /**
     * writes code which performs label command
     * @param label label to write
     */
    public void writeLabel(String label) {
        writer.append("(").append(label).append(")\n");
    }

    /**
     * writes code which performs goto command
     * @param label label to go to
     */
    public void writeGoto(String label) {
        writer.append("@").append(label).append("\n0;JMP\n");
    }

    /**
     * writes code which performs if-goto command
     * @param label label to go if
     */
    public void writeIf(String label) {
        writer.append("@SP\nM=M-1\nA=M\nD=M\n@").append(label).append("\nD;JNE\n");
    }

    /**
     * writes code which performs function command
     * @param functionName function name tp write
     * @param numVars numbr of vars to the function
     */
    public void writeFunction(String functionName, int numVars) {
        writer.append("(").append(functionName).append(")\n");
        for (int i = 0; i < numVars; i++) {
            writePushPop("C_PUSH", "constant", 0);
        }
    }

    /**
     * writes code which performs call command
     * @param functionName function name to go ti
     * @param numVars num of vars to the function
     */
    public void writeCall(String functionName, int numVars) {
        String ret = fileName + "$ret." + callCounter;
        callCounter++;
        writePushFromSrc(ret, "A");
        writePushFromSrc("LCL", "M");
        writePushFromSrc("ARG", "M");
        writePushFromSrc("THIS", "M");
        writePushFromSrc("THAT", "M");
        writer.append("@5\nD=A\n@SP\nD=M-D\n@").append(String.valueOf(numVars)).append("\nD=D-A\n@ARG\nM=D\n@SP\nD=M\n@LCL\nM=D\n");
        writeGoto(functionName);
        writeLabel(ret);
    }

    /**
     * writes code which performs return command
     */
    public void writeReturn() {
        writer.append("@LCL\nD=M\n@R13\nM=D\n@5\nA=D-A\nD=M\n@R14\nM=D\n@SP\nM=M-1\n@SP\nA=M\nD=M\n@ARG\nA=M\n" +
                "M=D\n@ARG\nD=M\n@SP\nM=D+1\n");
        this.writePlacement(1, "THAT");
        this.writePlacement(2, "THIS");
        this.writePlacement(3, "ARG");
        this.writePlacement(4, "LCL");
        writer.append("@R14\nA=M\n0;JMP\n");
    }

    public void close() {
        writer.append("(AND)\n@AND\n0;JMP");
        writer.close();
    }

    /**
     *  writes code which pushes RAM[src] to RAM[SP]
     */
    private void writePushFromSrc(String src, String address) {
        writer.append("@" + src + "\nD=" + address + "\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
    }

    /**
     * writes code which place RAM[R13-num] in RAM[dest]
     */
    private void writePlacement(int num, String dest) {
        writer.append("@" + num + "\nD=A\n@R13\nA=M-D\nD=M\n@" + dest + "\nM=D\n");
    }
}