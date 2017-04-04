import kvm.KVM;
import utils.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static final int MAX_PHYSICAL_NODES = 300;
    public static final int MAX_VIRTUAL_NODES = 20;

    public static final String ROOT_OUTPUT_FOLDER = "output/";
    public static final String CHMOD_SCRIPT = "chmod.sh";

    public static void main(String[] args) {
        System.out.println();
        System.out.println("Creating root output directory/cleaning it out");
        createRootDirectory();
        createChmodScript();
        createKVMFiles();
    }

    public static void createChmodScript() {
        try {
            Path chmodScriptPath = Paths.get(ROOT_OUTPUT_FOLDER + CHMOD_SCRIPT);
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(chmodScriptPath);

            FileUtils.writeLine(bufferedWriter, "#!/bin/bash");
            FileUtils.writeLine(bufferedWriter, "chmod -R +x .");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createRootDirectory() {
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(ROOT_OUTPUT_FOLDER));
        KVM.createKvmDirectories(ROOT_OUTPUT_FOLDER);
    }

    public static void createKVMFiles() {
        int physicalNodes = 0;
        int vmsPerNode = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Setting KVM config");
        while (physicalNodes < 1 || physicalNodes > MAX_PHYSICAL_NODES) {
            System.out.print("Please enter in number of physical nodes (1-" + MAX_PHYSICAL_NODES + "): ");
            try {
                physicalNodes = Integer.parseInt(br.readLine());
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
            if (physicalNodes < 1 || physicalNodes > MAX_PHYSICAL_NODES) {
                System.out.println("Invalid input.");
            }
        }
        System.out.println("Number of physical nodes: " + physicalNodes);

        while (vmsPerNode < 1 || vmsPerNode > MAX_VIRTUAL_NODES) {
            System.out.print("Please enter in number of virtual nodes per physical node (1-" + MAX_VIRTUAL_NODES + "): ");
            try {
                vmsPerNode = Integer.parseInt(br.readLine());
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
            if (vmsPerNode < 1 || vmsPerNode > MAX_VIRTUAL_NODES) {
                System.out.println("Invalid input.");
            }
        }
        System.out.println("Number of VMs per physical node: " + vmsPerNode);

        System.out.println("Creating files");
        System.out.println("");
        KVM.step0(ROOT_OUTPUT_FOLDER, physicalNodes);
        KVM.step1(ROOT_OUTPUT_FOLDER);
        KVM.step2(ROOT_OUTPUT_FOLDER);
        KVM.step3(ROOT_OUTPUT_FOLDER, physicalNodes, vmsPerNode);
        KVM.step4(ROOT_OUTPUT_FOLDER, physicalNodes, vmsPerNode);
    }

}
