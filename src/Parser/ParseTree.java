package Parser;

import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ParseTree {

    private String nameMask = "(\\b\\D[\\w\\d\\_]*\\b)"; //маска для "имя_узла"
    private String valueMask = "((\"|“)\\b[^\\n\"“”]+\\b(\"|”))"; //маска для "значения_узла"
    private String nodeMask = "((\\b\\D[\\w\\d\\_]*\\b))(\\s*=\\s*)(((\"|“)\\b[^\\n\"“”]+\\b(\"|”))|(список))"; //маска для узла
    private String listMask = "((({\\s*)\\b)((((\\b\\D[\\w\\d\\_]*\\b))(\\s*=\\s*)(((\"|“)\\b[^\\n\"“”]+\\b(\"|”))|(список)))(\\s*))+(}))"; //маска списка

    private String getResult = ""; //результат работы класса
    private Node root = null; //корень
    private Node current = null; //текущий узел
    private int id_current = 0; //id_текущего элемента
    

    //узел дерева
    private class Node{
        int id;
        int id_parent;
        String name;
        String value;
        Node Parent;
        Vector Children;


        private Node(int id, int id_parent, String name, String value, Node parent){
            this.id = id;
            this.id_parent = id_parent;
            this.name = name;
            this.value = value;
            this.Parent = parent;
            this.Children = null;
        }
    }

    //конструктор по умолчанию
    public ParseTree () {
    }

    //метод, заполняющий дерево парсинга
    private void fillParseTree (String itemsData) {

        String tempItems = "";
        Scanner items = new Scanner(itemsData);

        while (items.hasNextLine()){
            tempItems = items.nextLine();

            if(root == null){
                id_current++;

                root = new Node(0, 0, "root", "$", null); //инициализируем корень дерева

                if(tempItems.matches(nameMask) && !tempItems.matches(valueMask)) {
                    root.Children = new Vector(0, 1);
                    root.Children.addElement(new Node(id_current, root.id, tempItems, "", root));
                    current = (Node) root.Children.lastElement();
                }
                else if(tempItems.equals("{") || tempItems.matches(valueMask)){

                    if (tempItems.matches(valueMask)){
                        current.value = tempItems;
                        getResult += "(" + current.id + " , " + current.id_parent + " , " + current.name + " , " + current.value + " )\n";
                    }
                    else {
                        root.Children = new Vector(0, 1);
                        root.Children.addElement(new Node(id_current, root.id, "", "", root));
                        current = (Node)current.Children.lastElement();
                    }
                }

                }
            else {

                if (tempItems.matches(nameMask) && !tempItems.matches(valueMask)){
                    if(current.id != id_current){
                        id_current++;
                        current.Children.addElement(new Node(id_current, current.id, tempItems, "", current));
                        current = (Node)current.Children.lastElement();
                    }
                    else {

                    id_current++;
                    current.name = tempItems;
                    current.id = id_current;
                    //current.value = "\""; // " то узел существует
                    }
                }
                else if(tempItems.equals("{") || tempItems.matches(valueMask)){

                    if (tempItems.matches(valueMask)){
                        current.value = tempItems;
                        getResult += "(" + current.id + ", " + current.id_parent + ", " + current.name + ", " + current.value + ")\n";
                        current = current.Parent;
                    }
                    else {
                       // current.value = "";
                        getResult += "(" + current.id + ", " + current.id_parent + ", " + current.name + ", " + current.value + ")\n";
                        current.Children = new Vector(0, 1);
                        current.Children.addElement(new Node(id_current, current.id, "", "", current));
                        current = (Node)current.Children.lastElement();
                    }
                }
                else if(tempItems.equals("}")){
                    current = current.Parent;
                }

            }

        }
    }

    //метод возвращающий результат работы парсера
    public String parsingData(String data){
        String result = "Неверный формат данных" ;

        if (isValidData(data)){
            fillParseTree(getItemsData(data));
            result = getResult;
        }

        return result;
    }

    //получение высоты дерева
    private static int getTreeHeight(String data){
//char []symbol = data.toCharArray();

        int treeHeight = 0;
        int currentHeight = 0;

        for (int i = 0; i < data.length(); i++){

            if (data.charAt(i) == '{'){
                currentHeight++;
                if(currentHeight >= treeHeight)
                    treeHeight = currentHeight;
            }
            else if(data.charAt(i) == '}'){
                currentHeight--;
            }
        }
        return treeHeight;
    }

    //проверка данных на корректность
    public boolean isValidData(String data){
        int treeHeight = getTreeHeight(data);
        String pattern = getDataMask(treeHeight);
        return data.matches(pattern);
    }

    //получение маски для проверки данных
    private String getDataMask(int heightTree){
        String[]arr = new String[heightTree];
        String resultMask = "((\\b\\D[\\w\\d\\_]*\\b))(\\s*=\\s*)(((\"|“)\\b[^\\n\"“”]+\\b(\"|”))|(список))";

        for (int i = 0; i < heightTree; i++){
            if(i == 0){
                arr[i] = "(((\\{\\s*)\\b)((((\\b\\D[\\w\\d\\_]*\\b))(\\s*=\\s*)(((\"|“)\\b[^\\n\"“”]+\\b(\"|”))|(список)))(\\s*))+(\\}))";
            }
            else{

                arr[i] = "(((\\{\\s*)\\b)((((\\b\\D[\\w\\d\\_]*\\b))(\\s*=\\s*)(((\"|“)\\b[^\\n\"“”]+\\b(\"|”))|" + arr[i-1] + "))(\\s*))+(\\}))";
            }
        }

        if (heightTree != 0){
            resultMask = "\\s*((\\b\\D[\\w\\d\\_]*\\b))(\\s*=\\s*)(((\"|“)\\b[^\\n\"“”]+\\b(\"|”))|" + arr[heightTree-1] + ")\\s*";
        }

        return resultMask;
    }

    //получение элементов исходных данных
    private String getItemsData(String data){

        String itemsData = "";
        String tempItem = "";

        for (int i = 0; i < data.length(); i++){
            tempItem += data.charAt(i);

            if(tempItem.equals(" ")){
                tempItem = "";
            }

            if(tempItem.equals("=") || tempItem.equals("{") || tempItem.equals("}") ){
                itemsData += tempItem + "\n";
                tempItem = "";
            }else if (tempItem.matches(nameMask) && !tempItem.matches(valueMask) && (data.charAt(i+1) == ' ' || data.charAt(i+1) == '=' || data.charAt(i+1) == '[')){

                itemsData += tempItem + "\n";
                tempItem = "";
            }else if(tempItem.matches(valueMask)){
                itemsData += tempItem + "\n";
                tempItem = "";
            }
            if(tempItem.equals("")){
                tempItem = "";
            }
        }

        return itemsData;
    }
}

