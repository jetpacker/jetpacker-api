package io.jetpacker.api.web.command

/**
 * Created by donny on 30/11/16.
 */
class JetpackerData {
    MachineData ubuntu

    KitData openjdk
    KitData node
    KitData guard

    List<ContainerData> databaseEngines
    List<ContainerData> queueEngines
    List<ContainerData> searchEngines
}
