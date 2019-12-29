package Doogle;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author KONIN
 */
public class FileSplitter {

    private TextFileFilter filter = new TextFileFilter();
    private PrintWriter writer;
    private File[] inputFiles;
    private File splitDir;

    public FileSplitter(String data) {
        File f = new File(data);
        if (f.isDirectory()) {
            inputFiles = f.listFiles();
        } else if (f.isFile()) {
            inputFiles = new File[1];
            inputFiles[0] = f;
        }
    }

    public ArrayList<File> split() throws IOException {

        ArrayList<File> filesDone = new ArrayList();
        for (File file : inputFiles) {
            if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
                filesDone.add(file);
                splitDir = new File(LuceneConstants.SPLITDIR + "\\" + file.getName());
                splitDir.mkdir();
                
                /*String fileString = new String(Files.readAllBytes(Paths.get(file.toURI())));
                 writer = new PrintWriter(file);
                 writer.print(fileString);
                 writer.close();*/

                Document doc = Jsoup.parse(file, "UTF-8");
                Elements reuters = doc.select("reuters");
                for (Element e : reuters) {
                    Document article = Jsoup.parse(e.toString());
                    writer = new PrintWriter(splitDir + "\\" + article.select("reuters").attr("newid") + ".txt");
                    writer.println(article.select("places").text());
                    writer.println(article.select("people").text());
                    writer.println(article.title());
                    if (article.select("text").first().attr("type").isEmpty() || article.select("text").first().attr("type").equals("unproc")) {
                        writer.print(article.select("dateline").first().nextSibling().toString());
                    } else {
                        writer.print(" ");
                    }

                    /*
                     writer = new PrintWriter(splitDir + "\\" + e.attr("newid") + ".txt");
                     writer.println(PrepareText.stem(e.getElementsByTag("places").html()));
                     writer.println(e.getElementsByTag("people").text());
                     writer.println(e.getElementsByTag("title").text());
                     writer.print(e.getElementsByTag("article").text());*/
                    writer.close();
                }
                
            }
        }
        return filesDone;
    }
}
