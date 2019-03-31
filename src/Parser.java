import java.util.Scanner;

/**
 * Class responsible for parsing and updating fields for translation during the
 * VM -> hack assembly process.
 *
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

    public Parser(String fileName) {
        
    }

}
