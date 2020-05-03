package Parser;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Parser {

    public static void main(String[] args) throws IOException {

        ParseTree test = new ParseTree();
        String data = "";

        Scanner scanner = new Scanner(new FileInputStream("source.txt"));
        while (scanner.hasNextLine()){
            data += scanner.nextLine();
        }

        String result = test.parsingData(data);
        if (test.isValidData(data)){

        try(FileWriter writer = new FileWriter("result.txt", false))
        {
            writer.write(result);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }

    }
        else{
            System.out.println(test.parsingData(result));
        }
    }
}
