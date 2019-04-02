import sun.tools.java.SyntaxError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Generates assembly code from the parsed VM command.
 *
 * @author Jay Montoya
 * @version 1.0
 */
public class CodeWriter {

    /** connection to the output file where our hack assembly code will be written **/
    private PrintWriter outputFile;
    private HashMap<String, String> arithmeticMapper;
    private String outputFileName;

    /**
     * Opens the output file stream and gets ready to write to it.
     * @param fileName the name of the desired output file as a string
     */
    public CodeWriter(String fileName) {

       // next block of code might throw an exception
        try {

            // establish the connection to the output file
            outputFileName = fileName;
            outputFile = new PrintWriter(fileName);

        } catch (FileNotFoundException e) { // catch that shit

            // exception handling technique
            e.printStackTrace();
            System.exit(0); // force quit

        }

        arithmeticMapper = new HashMap<>();

        // build the arithmetic mapper
        arithmeticMapper.put("add", "@SP\n" +
                        "A = M - 1\n" +
                        "D = M\n" +
                        "A = A -1\n" +
                        "M = M + D\n" +
                        "@SP\n" +
                        "M = A + 1\n");
        arithmeticMapper.put("sub", "@SP\n" +
                "AM = M - 1\n" +
                "D = M\n" +
                "A = A - 1\n" +
                "M = M - D\n");
        arithmeticMapper.put("neg", "@SP\n" +
                "A = M - 1\n" +
                "M = !M\n");
        arithmeticMapper.put("eq", "@SP\n" +
                "AM = M - 1\n" +
                "D = M\n" +
                "A = A - 1\n" +
                "D = M - D\n" +
                "@CONTINUE\n" +
                "D;JNE\n" +
                "@SP\n" +
                "A = A - 1\n" +
                "M = -1\n" +
                "@END\n" +
                "0;JMP\n" +
                "(CONTINUE)\n" +
                "@SP\n" +
                "A = A - 1\n" +
                "M = 0\n" +
                "(END)\n");
        arithmeticMapper.put("gt", "@SP\n" +
                "AM = M - 1\n" +
                "D = M\n" +
                "A = A - 1\n" +
                "D = M - D\n" +
                "@TRUE\n" +
                "D;JGT\n" +
                "@SP\n" +
                "A = M - 1\n" +
                "M = 0\n" +
                "@END\n" +
                "0;JMP\n" +
                "(TRUE)\n" +
                "@SP\n" +
                "A = M - 1\n" +
                "M = -1\n" +
                "(END)\n");
        arithmeticMapper.put("lt", "@SP\n" +
                "AM = M - 1\n" +
                "D = M\n" +
                "A = A - 1\n" +
                "D = M - D\n" +
                "@TRUE\n" +
                "D;JLT\n" +
                "@SP\n" +
                "A = M - 1\n" +
                "M = 0\n" +
                "@END\n" +
                "0;JMP\n" +
                "(TRUE)\n" +
                "@SP\n" +
                "A = M - 1\n" +
                "M = -1\n" +
                "(END)\n");
        arithmeticMapper.put("and", "@SP\n" +
                "AM = M - 1\n" +
                "D = M\n" +
                "A = A - 1\n" +
                "M = M&D\n");
        arithmeticMapper.put("or", "@SP\n" +
                "AM = M - 1\n" +
                "D = M\n" +
                "A = A - 1\n" +
                "M = M|D\n");

    }

    /**
     * Writes to the output file the assembly code that implements the given input command.
     * @param command the given input command as a string
     */
    public void writeArithmetic(String command) {
        if (arithmeticMapper.containsKey(command)) {
            String code = "// " + command + "\n";
            code = code + arithmeticMapper.get(command) + "\n";

            outputFile.write(code);
            outputFile.flush();
        } else {
            // do nothing
        }
    }

    /**
     * Writes to the output file the assembly code that implements the given command where
     * the given command is either C_PUSH or C_POP.
     * @param commandType the command type as the enumerated data type 'CommandType'
     */
    public void writePushPop(CommandType commandType, String segment, int index) {
        String code = "// " + commandType  + " " + segment + " " + index + "\n";

        // if we're dealing with local, argument, this, or that
        if (segment.equals("local") ||
            segment.equals("argument") ||
            segment.equals("this") ||
            segment.equals("that")) {

            // pushing and popping to these 4 segments use the same code for
            // addr = LCL + arg2
            code = code + "@" + index + "\n" +
                    "D = A\n" +
                    "@" + getSymbolFromWord(segment) + "\n" +
                    "A = M + D\n" +
                    "D = M\n" +
                    "\n";

            // determind whether we're pushing or popping
            if (commandType == CommandType.C_PUSH) {
                code = code + "@SP\n" +
                        "A = M\n" +
                        "M = D\n" +
                        "@SP\n" +
                        "M = M + 1\n";
            } else if (commandType == CommandType.C_POP) {
                code = code + "D = A\n" +
                        "@addr\n" +
                        "M = D\n" +
                        "@SP\n" +
                        "AM = M - 1\n" +
                        "D = M\n" +
                        "@addr\n" +
                        "A = M\n" +
                        "M = D\n";
            } else {
                // well then why am I in this method?
            }

        } else if (segment.equals("constant")) {
            // we can only push these constants
            code = code + "@" + index + "\n" +
                    "D = A\n" +
                    "@SP\n" +
                    "AM = M + 1\n" +
                    "A = A - 1\n" +
                    "M = D\n";
            // TODO handle exceptions when someone POP's a constant

            if (VMTranslator.DEBUG) System.out.println("\t\tcodeWriter - > WRITING CONSTANT CODE");

        } else if (segment.equals("static")) {
            switch(commandType){
                case C_POP:
                    code = code + "@SP\n" +
                            "AM = M-1\n" +
                            "D = M\n" +
                            "@" + outputFileName.substring(10,outputFileName.indexOf('.')) + "." + index + "\n" +
                            "M = D\n";
                    break;
                case C_PUSH:
                    code = code + "@" + outputFileName.substring(10,outputFileName.indexOf('.')) + "." + index + "\n" +
                            "D = M\n" +
                            "@SP\n" +
                            "AM = M + 1\n" +
                            "A = A - 1\n" +
                            "M = D\n";
                    break;
            }
        } else if (segment.equals("temp")) {
            // addr = TEMP + arg2
            code = code + "@" + index + "\n" +
                    "D = A\n" +
                    "@5\n" +
                    "D = A + D\n" +
                    "@addr\n" +
                    "D = M\n";

            if (commandType == CommandType.C_PUSH) {
                code = code + "@SP\n" +
                        "A = M\n" +
                        "M = D\n" +
                        "@SP\n" +
                        "M = M + 1\n";
            } else if (commandType == CommandType.C_POP) {
                code = code + "D = A\n" +
                        "@addr\n" +
                        "M = D\n" +
                        "@SP\n" +
                        "AM = M - 1\n" +
                        "D = M\n" +
                        "@addr\n" +
                        "A = M\n" +
                        "M = D\n";
            } else {
                // well then why am I in this method?
            }
            //TODO: check for out of bounds with temp

        } else if (segment.equals("pointer")) {

            String thisOrThat = (index == 0) ? getSymbolFromWord("this")
                    : getSymbolFromWord("that");

            if (commandType == CommandType.C_PUSH) {
                code = code + "@" + thisOrThat + "\n" +
                        "D = M\n" +
                        "@SP\n" +
                        "AM = M + 1\n" +
                        "A = A - 1\n" +
                        "M = D\n";
            } else if (commandType == CommandType.C_POP) {
                code = code + "@SP\n" +
                        "AM = M - 1\n" +
                        "D = M\n" +
                        "@" + thisOrThat + "\n" +
                        "M = D\n";
            } else {
                // well then why am I in this method?
            }
        }

        code = code + "\n";

        outputFile.write(code);
        outputFile.flush();
    }

    private String getSymbolFromWord(String segment) {

        String returnThis = "";

        switch(segment) {
            case "local":
                returnThis = "LCL";
                break;
            case "argument":
                returnThis = "ARG";
                break;
            case "this":
                returnThis = "THIS";
                break;
            case "that":
                returnThis = "THAT";
                break;
            default:
                //throw new SyntaxException();
        }
        return returnThis;
    }

    /**
     * Closes the output file.
     */
    public void close() { outputFile.close(); }


}
