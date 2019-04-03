import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Class responsible for parsing and updating fields for translation during the
 * VM -> hack assembly process.
 *
 * - Handles the parsing of a single .vm file.
 * - Reads a VM command, parses it into it's lexical components, and provides convenient
 *      access to these components.
 * - Ignores all whitespace and comments.
 *
 * @author Jay Montoya
 * @version 1.0
 */
public class Parser {

    /** the first argument of the VM command (ex. 'pop local') **/
    private String arg1;

    /** the memory segment if applicable */
    private String memSeg;

    /** the second argument of the VM command (usually having to do with registers) **/
    private int arg2;



    /** a scanner object to be used to read the input file **/
    private Scanner inputFile;

    /** the type of command the parser has encounters (see CommandType.java) **/
    private CommandType commandType;

    /** raw line of the file **/
    private String rawLine;
    private String cleanLine;
    private int lineNumber;


    /**
     * Constructor for a parser object given the file name
     * @param fileName the path of the file you wish to read
     */
    public Parser(String fileName) {
        // initialize the scanner field
        try {
            inputFile = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lineNumber = 0;
    }

    /**
     * Simple method for determining if the parser can parse more lines
     * @return a boolean value indicating if the parse can continue.
     */
    public boolean hasMoreCommands() {
        return inputFile.hasNextLine();
    }

    /**
     * Void method responsible for advancing and parsing the given file.
     * Reads the next command from the input and makes it the current command.
     * Should be called only if hasMoreCommands() is true. Initially, there is
     * no current command
     */
    public void advance() {
        // get the net raw line.
        rawLine = inputFile.nextLine();

        cleanLine();

        if (cleanLine.equals("")) {
            //if (VMTranslator.DEBUG) System.out.println("\t\t\tEMPTY LINE");
            //System.out.println("Unrecognized Command!");
            commandType = CommandType.C_NONE;
            setArg1("");
            memSeg = "";
            return;
        } else {
            lineNumber++;
        }

        // parse everything
        try {
            parse();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // show results
        if (VMTranslator.DEBUG && commandType != CommandType.C_NONE) System.out.println("\tadvance() LINE " + lineNumber + " [ "
                + cleanLine + " ] // " + getCommandType() + " // " + arg1 + " " + memSeg + " " + arg2);


    }


    /**
     * Helper method for determining the command type of the line
     */
    private void parse() throws Exception {

        StringTokenizer st = new StringTokenizer(cleanLine);
        String firstWord = st.nextToken(" ");

        if (validateArithmetic(firstWord)) {
            // then it is an arithmetic command
            arg1 = firstWord;
            commandType = CommandType.C_ARITHMETIC;

            // checking for extraneous syntax
            if (st.hasMoreTokens()) {
                String badToken = st.nextToken();
                throw new VMTranslatorException("[ILLEGAL SYNTAX]: arithmetic commands must be stand alone -> found '"
                        + badToken + "'");
            }

        } else if (firstWord.equals("push")) {
            String secondWord = st.nextToken(" ");
            if (!validateMemSegment(secondWord)) {
                throw new VMTranslatorException("[INVALID MEMORY SEGMENT]: '" + secondWord + "' is not a validated memory segment");
            }

            commandType = CommandType.C_PUSH;
            setArg1(firstWord);
            memSeg = secondWord;

            // get the specific number
            setArg2(Integer.parseInt(st.nextToken()));

        } else if (firstWord.equals("pop")) {
            String secondWord = st.nextToken(" ");
            if (!validateMemSegment(secondWord)) {
                throw new VMTranslatorException("[INVALID MEMORY SEGMENT]: '" + secondWord + "' is not a validated memory segment");
            }
            commandType = CommandType.C_POP;
            memSeg = secondWord;



            // get the specific number
            setArg2(Integer.parseInt(st.nextToken()));

            // check for popping a constant
            if (memSeg.equals("constant")) {
                throw new VMTranslatorException("[ILLEGAL COMMAND]: attempt to pop constant " +  arg2);
            }

        } else {
            throw new VMTranslatorException("[INVALID COMMAND]: unrecognized command '" + firstWord + "'");
        }
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getMemSeg() {
        return memSeg;
    }


    public String getLinesRead() { return lineNumber + ""; }


    public boolean validateArithmetic(String word) {
        return (word.equals("add") ||
                word.equals("sub") ||
                word.equals("neg") ||
                word.equals("eq") ||
                word.equals("gt") ||
                word.equals("lt") ||
                word.equals("and") ||
                word.equals("or") ||
                word.equals("not") );
    }

    public boolean validateMemSegment(String word) {
        return (word.equals("local") ||
                word.equals("argument") ||
                word.equals("this") ||
                word.equals("that") ||
                word.equals("constant") ||
                word.equals("static") ||
                word.equals("pointer") ||
                word.equals("temp") );
    }






    /**
     * Helper method for cleaning the line contents/
     * @return a String representing a clean line in the file.
     */
    public void cleanLine() {
        cleanLine = rawLine.trim();
        int index = cleanLine.indexOf("//");



        cleanLine = (index != -1)
                ? cleanLine.substring(0, index).trim().replace(" ","")
                : cleanLine.trim();
    }


    /**
     * Accessor method for arg1. Returns the first argumnt of the
     * command. In the case of C_ARITHMETIC, the command itself,
     * (add, sub, etc.) is returned. Should not be called if the
     * current command is C_RETURN.
     * @return arg1
     */
    public String arg1() {
        return arg1;
    }

    /** Accessor method for arg2. Returns the second argument of the
     * current command. Should be called only if the current command
     * is C_PUSH, C_POP, C_FUNCTION, or C_CALL.
     * @return arg2
     */
    public int arg2() {
        return arg2;
    }

    /**
     * Mutator method for arg1
     * @param arg1 arg1
     */
    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    /**
     * Mutator method for arg2
     * @param arg2 arg2
     */
    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }


}
