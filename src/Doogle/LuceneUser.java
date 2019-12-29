package Doogle;

/**
 *
 * @author KONIN
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneUser {

    private Indexer indexer;
    private Searcher searcher;

    public LuceneUser() {
        File indexDir = new File(LuceneConstants.INDEXDIR);
        if (!indexDir.exists()) {
            indexDir.mkdir();
        }
        clearIndexFolder();
    }

    public void createIndex() throws IOException {
        clearIndexFolder();
        indexer = new Indexer(LuceneConstants.INDEXDIR);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(LuceneConstants.SPLITDIR, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
    }

    public  ArrayList<String[]> search(String searchQuery, int resultNumber, String[] fields) throws IOException, ParseException {
        TopDocs hits = null;
        ArrayList<String[]> rows = new ArrayList<>();
        
        searcher = new Searcher(LuceneConstants.INDEXDIR);
        hits = searcher.search(searchQuery, resultNumber, fields);
        System.out.println(hits.totalHits + " documents found for \"" + searchQuery);
        int i = 1;
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            rows.add(new String[]{String.valueOf(i), doc.get(LuceneConstants.ORIGINAL_TITLE), doc.get(LuceneConstants.FILE_PATH), String.valueOf(scoreDoc.score)});
            System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH) + " with score: " + scoreDoc.score);
            i++;
        }
        return rows;
    }

    public void deleteIndexFile(Term term) throws IOException, ParseException {
        indexer = new Indexer(LuceneConstants.INDEXDIR);
        System.out.println("Deleting documents with field '" + term.field() + "' with text '" + term.text() + "'");
        indexer.deleteDocuments(term);
        indexer.close();
    }

    public void clearIndexFolder() {
        File indexDir = new File(LuceneConstants.INDEXDIR);
        if (indexDir.list().length > 0) {
            File[] oldFiles = indexDir.listFiles();
            for (File f : oldFiles) {
                if (!f.isDirectory()) {
                    f.delete();
                }
            }
        }
    }
}
