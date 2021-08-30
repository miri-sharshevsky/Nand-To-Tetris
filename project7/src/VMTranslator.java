public class VMTranslator {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args[0]);
            CodeWriter codeWriter = new CodeWriter(args[0].replace(".vm", ".asm"));
            while (parser.hasMoreCommands()){
                parser.advanced();
                String commandType = parser.commandType();
                if(commandType==null){
                    continue;
                }
                if(commandType.equals("C_ARITHMETIC")){
                    codeWriter.writeArithmetic(parser.arg1());
                }else if(commandType.matches("C_PUSH|C_POP")){
                    codeWriter.writePushPop(parser.commandType(), parser.arg1(), Integer.parseInt(parser.arg2()));
                }

            }
            codeWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
