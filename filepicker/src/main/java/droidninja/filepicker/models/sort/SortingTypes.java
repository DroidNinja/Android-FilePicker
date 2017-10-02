package droidninja.filepicker.models.sort;

import java.util.Comparator;

import droidninja.filepicker.models.Document;

/**
 * Created by gabriel on 10/2/17.
 */

public enum SortingTypes {
    name(new NameComparator()), none(null);

    final private Comparator<Document> comparator;

    SortingTypes(Comparator<Document> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Document> getComparator() {
        return comparator;
    }
}
