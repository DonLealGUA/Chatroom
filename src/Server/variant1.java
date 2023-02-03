package Ordkedjor;

import java.io.*;
import java.util.*;

/**
 * Variant 1 av ordkedjor med BFS algoritm och oviktade noder.
 */
public class variant1 {

    /**
     * Läser in alla ord i Ordmängd.txt med en buffert reader och lägger i en arraylist.
     */
    public static List<String> readData() throws IOException {
        String fnam = "src/Ordmängd.txt";
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fnam)));
        ArrayList<String> words = new ArrayList<String>();
        while (true) {
            String word = r.readLine();
            if (word == null) { break; }
            assert word.length() == 5;  // indatakoll, om man kör med assertions på
            words.add(word);
        }
        return words;
    }

    /**
     * Läser in vilka ord den ska börja på och sluta på.
     * @return ArrayList med alla ord kombinationer från Testfall.txt
     */
    public static ArrayList<List<String>> readTestCase() throws IOException {
        String fnam = "src/Testfall.txt";
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fnam)));
        ArrayList<List<String>> testCaseData = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();

        while (true) {
            String line = r.readLine();
            if (line == null) { break; }
            assert line.length() == 11; // indatakoll, om man kör med assertions på
            List<String> items = new ArrayList<String>(Arrays.asList(line.split("\n")));
            test.add(items.get(0).toString());
        }
        for(int i = 0; i < test.size(); i++){
            List<String> items = new ArrayList<String>(Arrays.asList(test.get(i).split(" ")));
            testCaseData.add(items);
        }
        System.out.println(testCaseData);
        return testCaseData;

    }

    /**
     * Adjacency-listan är en ordnad Map där varje ord har en arraylist som innehåller alla andra ord som ligger intill (adjacent) den.
     * Det börjar med att den går igenom listans mängd för att lägga till alla ord i adj mappen.
     * Sedan itererar den genom varje ord i inmatningssträngen och lägger till dem i sina respektive array listor om de inte redan finns där.
     * Den kontrollerar sedan om det finns 4 eller fler kanter från ett ord till ett annat och lägger till dem i så fall.
     * @param words innehåller alla ord från Ordmängd.txt
     * @return returnerar en map som innehåller en string och en lista med strings.
     */
    public static Map<String, List<String>> getAdjacencyList(List<String> words) {
        Map<String, List<String>> adj = new HashMap<>();
        for(int i = 0; i < words.size(); ++i) {
            String word = words.get(i);
            adj.put(word, adj.getOrDefault(word, new ArrayList<>())); // Lägger in order samt en ny lista med ord som står den nära (adjacent)
            for(int j = 0; j < words.size(); ++j) {
                if(i == j) { //Ifall i är samma ord som J så betyder det att det finns en edge från x till y och hoppar över if-satsen.
                    continue;
                }
                int count = 0;
                String other = words.get(j);
                for(int k = 1; k < 5; ++k) {// itererar 5 gånger och lägger till 1 eller 0 beroende på om det ordet finns eller inte
                    count = count + ((other.indexOf(word.charAt(k)) != -1) ? 1 : 0);

                }
                // om villkoret är uppfyllt finns det en edge från x till y
                if(count >= 4) {
                    adj.get(word).add(other);
                }
            }
        }
        System.out.println(adj);
        return adj;
    }

    /**
     * BFS algoritmen för att försöka hitta den kortaste vägen mellan x och y.
     * Det börjar med att initiera en kö för att lägga in värden vi ska jämföra med för att försöka nå end.
     * Den initiera även ett Hashset visited som är till för att hålla reda på de besökta orden.
     * Därefter initierar den in två variabler start och slut som den ska försöka hitta den kortaste vägen mellan.
     * Ifall det finns en kant (edge) från det ordet till ett annat kommer det att läggas till i kön.
     * Därefter går den och räknar hur många steg det tar tills x == y.
     * @param adj Map som innehåller en string och en lista med strings.
     * @param test
     * @return
     */
    public static int bfs(Map<String,List<String>> adj, List<String> test) {
        LinkedList<String> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>(); // för att hålla reda på de besökta orden, eftersom grafen inte nödvändigtvis är en graf som är riktad och utan cykler.
        String start = test.get(0);
        String end = test.get(1);

        if(start.equals(end)){// IF startorder direkt == slut ordet returneras 0
            return 0;
        }

        queue.add(start);
        visited.add(start);
        int count = 0;
        while(!queue.isEmpty()){
            count++;
            int size = queue.size();
            for(int i = 0; i < size; ++i){
                String word = queue.poll(); //
                List<String> get = adj.get(word);
                for(int j = 0; j < get.size(); j++){
                    String val = get.get(j);
                    if(val.equals(end)){
                        return count; // returnerar hur många edges det tar att komma från x till y
                    }
                    if(!visited.contains(val)){
                        queue.add(val); // lägger bara till de ord som inte har besökts ännu.
                    }
                }
            }
        }
        return -1; // ifall det inte finns någon väg/edge så returnerar den -1
    }

    /**
     * Main metod som ska starta algoritmen.
     * Skapar en lista med alla ord.
     * Skapar en annan lista med alla testfallen. Det ska vara två ord med 5 bokstäver.
     * Skapar en Map som ska innehålla 2 strings per objekt som ligger i en arraylist.
     * I denna listan stoppar vi in värdena som vi får av metoden getAdjacencyList() där vi skickar in listan med alla ord.
     * Vi går till sist igenom hela listen av testdata och med bfs hittar den minsta vägen.
     */
    public static void main(String[] args) throws IOException {
        List<String> words = readData(); //få ordparen att testa
        List<List<String>> testData = readTestCase(); //få testdata som ska testas
        Map<String, List<String>> adjacencyList = getAdjacencyList(words);// skapa en adjacency List


        for (List<String> test : testData) {// testa varje case
            System.out.println(bfs(adjacencyList, test));
        }
    }
}
