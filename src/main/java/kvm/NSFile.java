package kvm;

import utils.FileUtils;
import utils.IPUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// step 0
public class NSFile {

    // edit these variables
    private static final String KVM_IMAGE_NAME = "UBNT-KVM";
    private static final String KVM_STARTUP_FILE_FOLDER = "/users/<username>/";

    // these variables do not need to be edited
    private static final String KVM_STEP_0_INSTRUCTION = "1-import-ns-file-into-deterlab-you-can-also-change-the-topology";
    private static final String KVM_NS_FILE = "kvm.ns";

    protected static void createNsFile(String root, int physicalNodes) {
        Path instructionFilePath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_0_DETERLAB_FOLDER + KVM_STEP_0_INSTRUCTION);
        FileUtils.createFile(instructionFilePath);
        try {
            Path nsFilePath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_0_DETERLAB_FOLDER + KVM_NS_FILE);
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(nsFilePath);

            FileUtils.writeLine(bufferedWriter, "set ns [new Simulator]");
            FileUtils.writeLine(bufferedWriter, "source tb_compat.tcl");
            bufferedWriter.newLine();
            FileUtils.writeLine(bufferedWriter, "# Image Name");
            FileUtils.writeLine(bufferedWriter, "set image_name \"" + KVM_IMAGE_NAME + "\"");
            bufferedWriter.newLine();
            FileUtils.writeLine(bufferedWriter, "# Startup File Folder");
            FileUtils.writeLine(bufferedWriter, "# e.g. /users/<username>/");
            FileUtils.writeLine(bufferedWriter, "set startup_folder \"" + KVM_STARTUP_FILE_FOLDER + "\"");
            bufferedWriter.newLine();
            FileUtils.writeLine(bufferedWriter, "# Nodes");
            for (int i = 0; i < physicalNodes; i++) {
                FileUtils.writeLine(bufferedWriter, "set node" + i + " [$ns node]");
                FileUtils.writeLine(bufferedWriter, "tb-set-node-os $node" + i + " $image_name");
                FileUtils.writeLine(bufferedWriter, "set startup_string \"sh $startup_folder\"");
                FileUtils.writeLine(bufferedWriter, "append startup_string \"node" + i + "-"
                        + NodeStartupScript.NODE_STARTUP_SCRIPT + " > VMnode" + i + ".log\"");
                FileUtils.writeLine(bufferedWriter, "tb-set-node-startcmd $node" + i + " \"$startup_string\"");
                bufferedWriter.newLine();
            }
            FileUtils.writeLine(bufferedWriter, "# Lan");
            bufferedWriter.write("set lan0 [$ns make-lan \"");
            for (int i = 0; i < physicalNodes; i++) {
                bufferedWriter.write("$node" + i + " ");
            }
            FileUtils.writeLine(bufferedWriter, "\" 10Gb 0.0ms]");
            bufferedWriter.newLine();
            FileUtils.writeLine(bufferedWriter, "# IP Addresses");
            for (int i = 0; i < physicalNodes; i++) {
                FileUtils.writeLine(bufferedWriter, "tb-set-ip-lan $node" + i + " $lan0 " + IPUtils.getPhysicalIP(i));
            }
            bufferedWriter.newLine();
            FileUtils.writeLine(bufferedWriter, "$ns rtproto Static");
            FileUtils.writeLine(bufferedWriter, "$ns run");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
