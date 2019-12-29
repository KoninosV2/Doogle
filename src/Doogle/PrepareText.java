package Doogle;

/**
 *
 * @author KONIN
 */
public final class PrepareText {

    private static final Stemmer stemmer = new Stemmer();
    private static final StringBuilder sb = new StringBuilder();

    public static String prepare(String text) {
        if (text.equals(" ") || text.isEmpty()) {
            return text;
        }
        String[] words = text.split(" ");
        sb.delete(0, sb.length());
        for (String word : words) {
            if (word.equals("AND") || word.equals("OR") || word.equals("NOT")) {
                sb.append(word);
                sb.append(" ");
            } else {
                word = word.toLowerCase();
                stemmer.add(word.toCharArray(), word.length());
                stemmer.stem();
                sb.append(stemmer.getResultBuffer(), 0, stemmer.getResultLength());
                sb.append(" ");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
