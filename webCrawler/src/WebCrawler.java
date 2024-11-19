import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private static Set<String> frontiera = new HashSet<String>();
    private static Set<String> visitati = new HashSet<String>();
    private static final int MAX_VISITATI = 3;

    public void addFrontiera(String url) {
        if (url != null && !url.isEmpty() && !frontiera.contains(url) && !visitati.contains(url)) {
            frontiera.add(url);
        }
    }

    public void addVisitati(List<String> urls) {
        for (String url : urls) {
            visitati.add(url);
        }
    }

    public String read(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            URL sito = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(sito.openStream()));
            StringBuilder buffer = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            in.close();
            return buffer.toString();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return null;
    }

    /* Regular expression per il recupero dei link */
    private Pattern pattern = Pattern.compile(
            "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
                    "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
                    "|mil|biz|info|mobi|name|aero|jobs|museum" +
                    "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
                    "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
                    "((\\?([-\\w~!$+|.,*:]|%[a-f\\d]{2})+=?" +
                    "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
                    "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d]{2})+=?" +
                    "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
                    "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

    public List<String> extractUrls(String input) {
        List<String> results = new ArrayList<String>();
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            results.add(matcher.group());
        }
        return results;
    }

    public void start() throws IOException {
        while (!frontiera.isEmpty() && visitati.size() < MAX_VISITATI) {
            String url = frontiera.iterator().next();
            System.out.println("Visito: " + url);
            String html = read(url);
            if (html != null) {
                // Estrai tutti i link
                List<String> links = extractUrls(html);
                addVisitati(links);
                // Aggiungi i link alla frontiera
                for (String link : links) {
                    addFrontiera(link);
                }
            }
            frontiera.remove(url); // Rimuovi l'URL appena visitato
            visitati.add(url); // Aggiungi l'URL alla lista dei visitati
            System.out.println("Visitati: " + visitati.size());
        }
    }

    public static void main(String[] args) throws IOException {
        // Aggiungi un seed URL
        WebCrawler crawler = new WebCrawler();
        crawler.addFrontiera("http://example.com");
        crawler.start();
    }
}
