package droidninja.filepicker.models.sort;

import java.util.Comparator;

import droidninja.filepicker.models.Document;

/**
 * Created by gabriel on 10/2/17.
 */

public class NameComparator implements Comparator<Document> {

    protected NameComparator() { }

    @Override
    public int compare(Document o1, Document o2) {
        return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
    }
}
