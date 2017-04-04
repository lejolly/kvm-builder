package kvm;

import utils.FileUtils;
import utils.IPUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NodeStartupScript {

    // resulting file is nodeX-NODE_STARTUP_SCRIPT
    protected static final String NODE_STARTUP_SCRIPT = "nodeStartup.sh";

    protected static void createNodeStartupScript(String root, int physicalNodes) {
        try {
            for (int j = 0; j < physicalNodes; j++) {
                Path nodeStartupScript = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_0_DETERLAB_FOLDER
                        + "node" + j + "-" + NODE_STARTUP_SCRIPT);
                BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(nodeStartupScript);

                FileUtils.writeLine(bufferedWriter, "#!/bin/sh");
                FileUtils.writeLine(bufferedWriter, "sudo addgroup libvirtd --gid 117");
                FileUtils.writeLine(bufferedWriter, "sudo addgroup kvm --gid 118");
                FileUtils.writeLine(bufferedWriter, "sudo useradd -u 109 -g 65534 -d /var/lib/misc -s /bin/false -c \"dnsmasq,,,\" dnsmasq");
                FileUtils.writeLine(bufferedWriter, "sudo useradd -u 110 -g 118 -d /var/lib/libvirt -s /bin/false -c \"Libvirt Qemu,,,\" libvirt-qemu");
                FileUtils.writeLine(bufferedWriter, "sudo useradd -u 111 -g 117 -d /var/lib/libvirt/dnsmasq -s /bin/false -c \"Libvirt Dnsmasq,,,\" libvirt-dnsmasq");
                FileUtils.writeLine(bufferedWriter, "sudo usermod -a -G libvirtd,kvm `id -un`");
                FileUtils.writeLine(bufferedWriter, "sudo chown -R root:kvm /dev/kvm");
                FileUtils.writeLine(bufferedWriter, "sudo /etc/init.d/libvirt-bin stop");
                FileUtils.writeLine(bufferedWriter, "sudo /etc/init.d/libvirt-bin start");
                bufferedWriter.newLine();
                for (int i = 0; i < physicalNodes; i++) {
                    if (i != j) {
                        FileUtils.writeLine(bufferedWriter, "sudo ip route add " + IPUtils.getVMSubnet(i) + " via " + IPUtils.getPhysicalIP(i));
                    }
                }
                bufferedWriter.newLine();
                FileUtils.writeLine(bufferedWriter, "exit");

                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
