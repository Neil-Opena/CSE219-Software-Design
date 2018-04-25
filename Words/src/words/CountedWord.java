package words;

public class CountedWord implements Comparable<CountedWord> {
    private final String word;
    private int count;
    
    public CountedWord(String w) {
        word = w;
	count = 0;
    }
    
    public String getWord() {
        return word;
    }

    public int getCount() {
	return count;
    }

    void tally() {
	count++;
    }
    
    @Override
    public int compareTo(CountedWord w) {
	int c1 = this.getCount();
	int c2 = w.getCount();
	if(c1 < c2)
	    return(11);
	else if (c1 > c2)
	    return(-1);
	else
	    return(0);
    }
}
