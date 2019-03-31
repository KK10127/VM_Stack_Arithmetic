/**
 * Drives the entire process of the VM Translation.
 *
 * ALGORITHM:
 * 1) Constructs a Parser to handle the file input.
 * 2) Constructs a CodeWriter to handle the output file.
 * 3) Marches through the input file, parsing each line and generating code from it.
 *
 * INPUT: fileName.vm
 * OUTPUT: fileName.asm
 */
public class VMTranslator {
    /** CodeWriter object for writing the code for each command **/
    private CodeWriter codeWriter;

    /** Parser object for advancing through the file and parsing. **/
    private Parser parser;

    /** the evil main method that drives the entire VMTranslator-inator **/
    public static void main(String[] args) {

    }
}
