package jetpacker.api.cache

import jetpacker.api.configuration.Box
import jetpacker.api.configuration.Container
import jetpacker.api.configuration.Kit

/**
 * Created by donny on 6/11/16.
 */
class Jetpacker {
    final List<Bucket<Box>> boxes = new ArrayList<>()
    final List<Bucket<Kit>> kits = new ArrayList<>()
    final List<Bucket<Container>> databases = new ArrayList<>()
    final List<Bucket<Container>> messageQueues = new ArrayList<>()
    final List<Bucket<Container>> searchEngines = new ArrayList<>()

    void clear() {
        boxes.clear()
        kits.clear()
        databases.clear()
        messageQueues.clear()
        searchEngines.clear()
    }
}
