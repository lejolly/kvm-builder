package kvm;

import utils.FileUtils;
import utils.IPUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// step 4
public class InstallVMScript {

    private static final String KVM_STEP_4_INSTRUCTION_1 = "1-ensure-that-you-have-debvm-base.qcow2-in-this-folder";
    private static final String KVM_STEP_4_INSTRUCTION_2 = "2-run-this-script-file-on-all-physical-nodes";
    private static final String VM_INSTALL_SCRIPT = "VM-install.sh";

    protected static void createInstallVMScript(String root, int physicalNodes, int vmsPerNode) {
        Path instructionFilePath1 = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_4_INSTALL_VM_FOLDER + KVM_STEP_4_INSTRUCTION_1);
        FileUtils.createFile(instructionFilePath1);
        Path instructionFilePath2 = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_4_INSTALL_VM_FOLDER + KVM_STEP_4_INSTRUCTION_2);
        FileUtils.createFile(instructionFilePath2);

        try {
            Path fileName = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_4_INSTALL_VM_FOLDER + VM_INSTALL_SCRIPT);
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(fileName);
            FileUtils.writeLine(bufferedWriter, "#!/bin/bash");
            FileUtils.writeLine(bufferedWriter, "virsh net-start private");
            bufferedWriter.newLine();
            for (int i = 0; i < physicalNodes; i++) {
                FileUtils.writeLine(bufferedWriter, "sudo ip route add " + IPUtils.getVMSubnet(i) + " via " + IPUtils.getPhysicalIP(i));
            }
            bufferedWriter.newLine();

            // copy base image
            FileUtils.writeLine(bufferedWriter, "sudo chown -R `whoami`:`id -g -n $whoami` /home/");
            FileUtils.writeLine(bufferedWriter, "mkdir -p /home/vms/deb");
            FileUtils.writeLine(bufferedWriter, "cp ./debvm-base.qcow2 /home/vms/deb/debvm-base.qcow2");
            bufferedWriter.newLine();

            // create child images
            for (int i = 1; i <= vmsPerNode; i++) {
                FileUtils.writeLine(bufferedWriter, "qemu-img create -b /home/vms/deb/debvm-base.qcow2 -f qcow2 /home/vms/deb/debvm-" + i + ".qcow2");
            }
            bufferedWriter.newLine();

            for (int i = 1; i <= vmsPerNode; i++) {
                FileUtils.writeLine(bufferedWriter, "virt-install \\");
                FileUtils.writeLine(bufferedWriter, "--import \\");
                FileUtils.writeLine(bufferedWriter, "--name debvm-" + i + " \\");
                FileUtils.writeLine(bufferedWriter, "--vcpus=1 \\");
                FileUtils.writeLine(bufferedWriter, "--ram 1024 \\");
                FileUtils.writeLine(bufferedWriter, "--disk path=/home/vms/deb/debvm-" + i + ".qcow2,format=qcow2 \\");
                FileUtils.writeLine(bufferedWriter, "--os-type linux \\");
                FileUtils.writeLine(bufferedWriter, "--graphics none \\");
                FileUtils.writeLine(bufferedWriter, "--autostart \\");
                FileUtils.writeLine(bufferedWriter, "--noautoconsole \\");
                FileUtils.writeLine(bufferedWriter, "--network network=private");
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
