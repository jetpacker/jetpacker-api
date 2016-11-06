package jetpacker.api.cache

import groovy.transform.CompileStatic
import jetpacker.api.configuration.Metadata

/**
 * Created by donny on 6/11/16.
 */
@CompileStatic
class Bucket<T extends Metadata> {
    final String label
    final List<T> items

    Bucket(String label, T item) {
        this.label = label
        items = [ item ].asImmutable()
    }

    Bucket(String label, List<T> items) {
        this.label = label
        this.items = items.asImmutable()
    }
}