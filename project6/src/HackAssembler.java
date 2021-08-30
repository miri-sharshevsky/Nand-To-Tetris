import java.io.*;

public class HackAssembler {
    public static void main(String[] args) throws IOException {

        int lineCounter = 0;
        int commandCounter = 16;
        SymbolTable table = new SymbolTable();
        table.addEntry("SCREEN", 16384);
        table.addEntry("KBD", 24576);
        table.addEntry("SP", 0);
        table.addEntry("LCL", 1);
        table.addEntry("ARG", 2);
        table.addEntry("THIS", 3);
        table.addEntry("THAT", 4);

        for (int i = 0; i < 16; i++) {
            table.addEntry("R" + i, i);
        }

        String programPath = args[0];
        //Scanner input = new Scanner(System.in);
        //System.out.println("enter your program path");
        //String programPath = input.nextLine();
        Paraser parser = new Paraser(programPath);

        File file = new File(programPath.replace(".asm", ".hack"));
        PrintWriter fileWriter = new PrintWriter(new FileWriter(file), true);
        while (parser.hasMoreCommands()) {
            parser.advanced();
            String commandType = parser.commandType();
            if (commandType != null) {
                if (commandType.equals("L_COMMAND")) {
                    table.addEntry(parser.symbol(), lineCounter);
                } else {
                    lineCounter++;
                }
            }
        }
        Paraser parser2 = new Paraser(programPath);
        while (parser2.hasMoreCommands()) {
            parser2.advanced();
            String commandType = parser2.commandType();
            if (commandType != null) {
                if (commandType.equals("A_COMMAND")) {
                    String symbol = parser2.symbol();
                    if (table.contains(symbol)) {
                        int address = table.getAddress(symbol);

                        String out = String.format("%016d", Long.parseLong(Long.toBinaryString(address)));
                        fileWriter.append(out);
                        fileWriter.append('\n');
                    } else {
                        if (symbol.chars().allMatch(Character::isDigit)) {
                            String out = String.format("%016d", Long.parseLong(Long.toBinaryString(Long.parseLong(symbol))));
                            fileWriter.append(out);
                            fileWriter.append('\n');
                        } else {
                            table.addEntry(symbol, commandCounter);

                            String out = String.format("%016d", Integer.parseInt(Integer.toBinaryString(commandCounter)));
                            fileWriter.append(out);
                            fileWriter.append('\n');
                            commandCounter++;
                        }

                    }
                } else if (commandType.equals("C_COMMAND")) {
                    String c = parser2.comp();
                    String d = parser2.dest();
                    String j = parser2.jump();
                    Code code = new Code();
                    String cc = code.comp(c);
                    String dd = code.dest(d);
                    String jj = code.jump(j);
                    String out = "111" + cc + dd + jj;
                    if (cc == null) {
                        out = "111" + "000000" + dd + jj;
                    }
                    if (dd == null) {
                        out = "111" + cc + "000" + jj;
                    }
                    if (jj == null) {
                        out = "111" + cc + dd + "000";
                    }
                    fileWriter.append(out);
                    fileWriter.append('\n');

                }
            }
        }
        fileWriter.close();
    }

}
