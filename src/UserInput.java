import java.io.*;

public class UserInput {

    // This class centralizes user input stream handling, so that if the
    // implementation needs to change it can in one place (Single Responsibility Principle).
    // Also, since System.in is a shared resource, Game will have responsibility
    // of distributing it to those that need it and finally closing it

    final private BufferedReader br;

    // Start the input stream when class is instantiated
    public UserInput() {
         br = new BufferedReader(new InputStreamReader(System.in));
    }

    // prompts the user for input, handles stream reading errors, and returns as a string
    public String readLine() {
        String userInput = "";
        try {
            userInput = br.readLine();
        } catch (IOException e) {
            System.err.println("Error reading line from input stream.");
        }
        // br.readLine() can return null in some cases (end of stream),
        // it seems appropriate to handle that here rather than
        // all classes that use UserInput to know that readLine can return null
        if (userInput == null) {
            System.err.println("User input stream is null.");
        }
        return userInput;
    }

    // reads a line from user input and converts it to an integer before returning
    public int readInt() {
        try {
            return Integer.parseInt(this.readLine());
        } catch (NumberFormatException e) {
            System.err.println("User input stream is not an integer.");
        }
        // if the input is not an integer or readLine returns null, default return -1
        // System.err will report both of these
        return -1;
    }

    // repeatedly prompts the user for a valid index from list size argument
    // which are printed as one-indexed. Finally returns index of the list (zero-indexed)
    public int getIndexWithinRange(int listSize) {

        int indexUserInput = 0;

        while (indexUserInput <= 0 || indexUserInput > listSize) {

            // reads an integer from user input
            indexUserInput = this.readInt();

            if (indexUserInput <= 0 || indexUserInput > listSize) {
                System.out.println("Please choose a number from the list (1 to " + (listSize) + ").");
            }
        }

        // returns zero-indexed
        return indexUserInput - 1;
    }

    // reads a line from user input and converts it to lower case before returning
    public String readLowerCaseString() {

        String userInput = this.readLine();
        if (userInput != null) {
            return userInput.toLowerCase();
        }
        System.err.println("toLowerCase received null input from readLine, returning empty string");
        // defaults to returning an empty string if readLine returns null
        return "";
    }

    // close the stream, Game is given responsibility for opening/closing the stream
    public void closeStream() {
        try {
            br.close();
        } catch (IOException e) {
            System.err.println("Error closing input stream.");
        }
    }
}
