package io.jetpacker.api.configuration.container

/**
 * Created by wolf on 15/2/17.
 */
enum Type {
    DataStore("Data Store"),
    MessageBroker("Message Broker"),
    SearchEngine("Search Engine")

    String label

    Type(String label) {
        this.label = label
    }
}