package kvm;

import utils.FileUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// step 1
public class InstallKVM {

    private static final String KVM_INSTALL_SCRIPT = "KVM-install.sh";

    protected static void createInstallScript(String root) {
        try {
            Path installScriptPath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_1_INSTALL_KVM_FOLDER + KVM_INSTALL_SCRIPT);
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(installScriptPath);

            FileUtils.writeLine(bufferedWriter, "#!/bin/bash");
            FileUtils.writeLine(bufferedWriter, "sudo apt-get -qq update");
            FileUtils.writeLine(bufferedWriter, "sudo apt-get -qq -y install qemu-kvm libvirt-bin virtinst bridge-utils");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
