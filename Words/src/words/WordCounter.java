package words;

import java.util.LinkedHashMap;
import java.util.Map;

public class WordCounter {
    //private final List<CountedWord> words;
    private final Map<Word, CountedWord> words;

    WordCounter() {
	//words = new LinkedList<>();
	words = new LinkedHashMap<>();
    }
    
    private CountedWord findWord(Word w) {
	if(words.containsKey(w)){
		return words.get(w);
	}
	return null;
//        for(int i = 0; i < words.size(); i++) {
//            CountedWord cw = words.get(i);
//            if(cw.getWord().equals(w))
//                return cw;
//        }
//        return null;
    }

    void countWord(Word w) {
        CountedWord cw = findWord(w);
	if(cw == null) {
	    cw = new CountedWord(w);
            //words.add(cw);
	    words.put(w, cw);
        }
	cw.tally();
    }

    int getCount(Word w) {
	CountedWord cw = findWord(w);
	if(cw == null)
	    return(0);
	else
	    return(cw.getCount());
    }

    int numWords() {
	return(words.size());
    }

    CountedWord [] sortWords() {
        //CountedWord[] wds = words.toArray(new CountedWord[0]);
	CountedWord[] wds = words.values().toArray(new CountedWord[0]);
	Quicksort.quickSort(wds);
	return(wds);
    }

}
