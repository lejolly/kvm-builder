package kvm;

import utils.FileUtils;
import utils.IPUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// step 3
public class NetworkAndRoutes {

    private static final String KVM_STEP_3_INSTRUCTION = "1-run-the-corresponding-script-on-each-physical-node";
    private static final String KVM_NETWORK_IP = "network-ip.txt";

    protected static void createNetworkAndRoutes(String root, int physicalNodes, int vmsPerNode) {
        Path instructionFilePath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_3_CREATE_NETWORK_AND_ROUTES_FOLDER + KVM_STEP_3_INSTRUCTION);
        FileUtils.createFile(instructionFilePath);

        Path folderPath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_3_CREATE_NETWORK_AND_ROUTES_FOLDER);
        try {
            Path filePath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM_NETWORK_IP);
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(filePath);
            FileUtils.printAndWriteLine(bufferedWriter, "KVM");
            for (int i = 0; i < physicalNodes; i++) {
                NetworkXML.writeNetworkXML(folderPath, i, vmsPerNode);
                createNetworkScript(folderPath, i);
                printIPs(bufferedWriter, i, vmsPerNode);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printIPs(BufferedWriter bufferedWriter, int nodeNumber, int vmsPerNode) throws IOException {
        FileUtils.printAndWriteLine(bufferedWriter, "node" + nodeNumber + ": " + IPUtils.getPhysicalIP(nodeNumber));
        FileUtils.printAndWriteLine(bufferedWriter, "\tdebvm-" + (1) + " - debvm-" + vmsPerNode + ": " +
                IPUtils.getVMStartIP(nodeNumber) + " - " + IPUtils.getVMEndIP(nodeNumber, vmsPerNode));
    }

    private static void createNetworkScript(Path path, int nodeNumber) {
        try {
            Path fileName = Paths.get(path.toString() + "/node" + nodeNumber + "-network.sh");
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(fileName);

            FileUtils.writeLine(bufferedWriter, "#!/bin/bash");
            FileUtils.writeLine(bufferedWriter, "virsh net-define ./xml-node" + nodeNumber + "-network.xml");
            FileUtils.writeLine(bufferedWriter, "virsh net-autostart private");
            FileUtils.writeLine(bufferedWriter, "virsh net-start private");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
