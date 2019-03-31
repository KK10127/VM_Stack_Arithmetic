import java.util.Scanner;

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

    /** the second argument of the VM command (usually having to do with registers) **/
    private int arg2;

    /** a scanner object to be used to read the input file **/
    private Scanner inputFile;

    /** the type of command the parser has encounters (see CommandType.java) **/
    private CommandType commandType;

    /**
     * Constructor for a parser object given the file name
     * @param fileName the path of the file you wish to read
     */
    public Parser(String fileName) {
        // initialize the scanner field
        inputFile = new Scanner(fileName);
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
