package Doogle;

/**
 *
 * @author KONIN
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    private IndexWriter writer;
    private File indexDir;

    public Indexer(String indexDirectoryPath) throws IOException {
        indexDir = new File(indexDirectoryPath);
        /*if (!indexDir.exists()) {
            indexDir.mkdir();
        }*/
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(indexDir.toPath());

        //create the indexer
        writer = new IndexWriter(indexDirectory, new IndexWriterConfig(new StandardAnalyzer()));
    }

    public void close() throws CorruptIndexException, IOException {
        writer.commit();
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();
        BufferedReader br = new BufferedReader(new FileReader(file));

        //index file contents
        
        Field placesField = new Field(LuceneConstants.PLACES, PrepareText.prepare(br.readLine()), TextField.TYPE_NOT_STORED);

        Field peopleField = new Field(LuceneConstants.PEOPLE, PrepareText.prepare(br.readLine()), TextField.TYPE_NOT_STORED);

        String originalTitle = br.readLine();
        Field titleField = new Field(LuceneConstants.TITLE, PrepareText.prepare(originalTitle), TextField.TYPE_NOT_STORED);

        Field bodyField = new Field(LuceneConstants.BODY, PrepareText.prepare(br.readLine()), TextField.TYPE_NOT_STORED);
        
        Field originalTitleField = new StringField(LuceneConstants.ORIGINAL_TITLE, originalTitle, Field.Store.YES);

        Field fatherField = new StringField(LuceneConstants.FATHER, file.getParentFile().getName(), Field.Store.YES);

        //index file name
        Field fileNameField = new StringField(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES);
        //index file path
        Field filePathField = new StringField(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

        document.add(placesField);
        document.add(peopleField);
        document.add(titleField);
        document.add(bodyField);
        
        document.add(originalTitleField);
        document.add(fatherField);

        document.add(fileNameField);
        document.add(filePathField);
        
        br.close();
        return document;
    }

    private void indexFile(File file) throws IOException {
        //System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter) throws IOException {

        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                createIndex(file.getAbsolutePath(), filter);
            } else if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
                indexFile(file);
            }
        }
        return writer.numDocs();
    }

    public void deleteDocuments(Term term) throws IOException, ParseException {
        System.out.println(writer.numDocs());
        System.out.println(writer.numRamDocs());

        writer.deleteDocuments(term);

        System.out.println(writer.numDocs());
        System.out.println(writer.numRamDocs());

    }
}
