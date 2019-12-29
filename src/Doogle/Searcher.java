package Doogle;

/**
 *
 * @author KONIN
 */
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    private final IndexSearcher indexSearcher;
    QueryParser queryParser;
    private Query query;

    public Searcher(String indexDirectoryPath) throws IOException {

        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));

        //queryParser = new QueryParser(LuceneConstants.BODY, new StandardAnalyzer());
    }

    public TopDocs search(String searchQuery, int resultNumber, String[] fields) throws IOException, ParseException {
        Occur[] flags = new Occur[fields.length];
        for (int i = 0; i < fields.length; i++) {
            flags[i] = BooleanClause.Occur.SHOULD;
        }
        query = MultiFieldQueryParser.parse(searchQuery, fields, flags, new StandardAnalyzer());
        System.out.println(query.toString());
        if (resultNumber == 0) {
            resultNumber = 1000000;
        }
        return indexSearcher.search(query, resultNumber);
    }

    /*public TopDocs vectorSearch(String searchQuery, int resultNumber, String[] fields) throws IOException, ParseException {
     queryParser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
     query = queryParser.parse(searchQuery);
     Occur[] flags = new Occur[fields.length];
     for (int i = 0; i < fields.length; i++) {
     flags[i] = BooleanClause.Occur.SHOULD;
     }
     queryParser.get
     query = MultiFieldQueryParser.parse(searchQuery, fields, flags, new StandardAnalyzer());
     System.out.println(query.toString());
     return indexSearcher.search(query, resultNumber);
     }

     public TopDocs booleanSearch(String searchQuery, int resultNumber, String[] fields) throws IOException, ParseException {
     Occur[] flags = new Occur[fields.length];
     for (int i = 0; i < fields.length; i++) {
     flags[i] = BooleanClause.Occur.SHOULD;
     }
     query = MultiFieldQueryParser.parse(searchQuery, fields, flags, new StandardAnalyzer());
     System.out.println(query.toString());

     return indexSearcher.search(query, resultNumber);
     }

     public TopDocs phraseSearch(String searchQuery, int resultNumber, String[] fields) throws IOException, ParseException {
     queryParser = new QueryParser(LuceneConstants.BODY, new StandardAnalyzer());

     query = queryParser.createPhraseQuery(LuceneConstants.BODY, searchQuery);
     System.out.println(query.toString());
     return indexSearcher.search(query, resultNumber);
     }*/
    public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }
}
