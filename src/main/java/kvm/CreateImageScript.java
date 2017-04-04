package kvm;

import utils.FileUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// step 2
public class CreateImageScript {

    private static final String KVM_STEP_2_INSTRUCTION_1 = "1-run-create-image-script-first";
    private static final String KVM_STEP_2_INSTRUCTION_2 = "2-then-run-compress-image-script";
    private static final String KVM_CREATE_IMAGE_SCRIPT = "create-image.sh";
    private static final String KVM_COMPRESS_IMAGE_SCRIPT = "compress-image.sh";

    protected static void createImageScript(String root) {
        Path instructionFilePath1 = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_2_CREATE_IMAGE_FOLDER + KVM_STEP_2_INSTRUCTION_1);
        FileUtils.createFile(instructionFilePath1);
        Path instructionFilePath2 = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_2_CREATE_IMAGE_FOLDER + KVM_STEP_2_INSTRUCTION_2);
        FileUtils.createFile(instructionFilePath2);
        try {
            Path createImageScriptPath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_2_CREATE_IMAGE_FOLDER + KVM_CREATE_IMAGE_SCRIPT);
            BufferedWriter bufferedWriter = FileUtils.createAndWriteToFile(createImageScriptPath);

            FileUtils.writeLine(bufferedWriter, "#!/bin/bash");
            FileUtils.writeLine(bufferedWriter, "echo please ensure that you have kvm installed");
            FileUtils.writeLine(bufferedWriter, "echo installing libguestfs");
            FileUtils.writeLine(bufferedWriter, "sudo apt-get -qq update");
            FileUtils.writeLine(bufferedWriter, "sudo apt-get -qq -y install software-properties-common");
            FileUtils.writeLine(bufferedWriter, "sudo add-apt-repository -y ppa:mpanella/libguestfs");
            FileUtils.writeLine(bufferedWriter, "sudo apt-get -qq update");
            FileUtils.writeLine(bufferedWriter, "echo select no configuration for postfix");
            FileUtils.writeLine(bufferedWriter, "echo select yes when asked to install supermin appliance");
            FileUtils.writeLine(bufferedWriter, "sudo apt-get -qq -y install libguestfs-tools");
            FileUtils.writeLine(bufferedWriter, "sudo chmod 0644 /boot/vmlinuz*");
            FileUtils.writeLine(bufferedWriter, "echo creating images in /home/vms/deb/");
            FileUtils.writeLine(bufferedWriter, "sudo chown -R `whoami`:`id -g -n $whoami` /home/");
            FileUtils.writeLine(bufferedWriter, "mkdir -p /home/vms/deb");
            FileUtils.writeLine(bufferedWriter, "echo creating debian image");
            FileUtils.writeLine(bufferedWriter, "virt-builder \\");
            FileUtils.writeLine(bufferedWriter, "--output /home/vms/deb/debvm-full.qcow2 \\");
            FileUtils.writeLine(bufferedWriter, "--size 8G \\");
            FileUtils.writeLine(bufferedWriter, "--format qcow2 \\");
            FileUtils.writeLine(bufferedWriter, "--firstboot-command \"dpkg-reconfigure openssh-server\" \\");
            FileUtils.writeLine(bufferedWriter, "--edit '/etc/default/grub:s/^GRUB_CMDLINE_LINUX_DEFAULT=.*/GRUB_CMDLINE_LINUX_DEFAULT=\"console=tty0 console=ttyS0,115200n8\"/' \\");
            FileUtils.writeLine(bufferedWriter, "--edit '/lib/systemd/system/serial-getty@.service:s{ExecStart=-/sbin/agetty}{ExecStart=-/sbin/agetty -a root}' \\");
            FileUtils.writeLine(bufferedWriter, "--run-command update-grub \\");
            FileUtils.writeLine(bufferedWriter, "--root-password password:root \\");
            FileUtils.writeLine(bufferedWriter, "debian-8");
            FileUtils.writeLine(bufferedWriter, "echo the username is: root and the password is also: root (probably not needed)");
            FileUtils.writeLine(bufferedWriter, "echo after installation, please run the three commands below to prepare the image for compression");
            FileUtils.writeLine(bufferedWriter, "echo you can also execute your own commands here to customise the base image");
            FileUtils.writeLine(bufferedWriter, "echo ------");
            FileUtils.writeLine(bufferedWriter, "echo dd if=/dev/zero of=/mytempfile");
            FileUtils.writeLine(bufferedWriter, "echo rm -f /mytempfile");
            FileUtils.writeLine(bufferedWriter, "echo shutdown now");
            FileUtils.writeLine(bufferedWriter, "echo ------");
            FileUtils.writeLine(bufferedWriter, "echo installing debian vm");
            FileUtils.writeLine(bufferedWriter, "virt-install \\");
            FileUtils.writeLine(bufferedWriter, "--import \\");
            FileUtils.writeLine(bufferedWriter, "--name debvm-full \\");
            FileUtils.writeLine(bufferedWriter, "--vcpus=1 \\");
            FileUtils.writeLine(bufferedWriter, "--ram 1024 \\");
            FileUtils.writeLine(bufferedWriter, "--disk path=/home/vms/deb/debvm-full.qcow2,format=qcow2 \\");
            FileUtils.writeLine(bufferedWriter, "--os-type linux \\");
            FileUtils.writeLine(bufferedWriter, "--graphics none");

            bufferedWriter.flush();
            bufferedWriter.close();

            Path compressImageScriptPath = Paths.get(root + KVM.KVM_ROOT_FOLDER + KVM.STEP_2_CREATE_IMAGE_FOLDER + KVM_COMPRESS_IMAGE_SCRIPT);
            bufferedWriter = FileUtils.createAndWriteToFile(compressImageScriptPath);

            FileUtils.writeLine(bufferedWriter, "#!/bin/bash");
            FileUtils.writeLine(bufferedWriter, "echo ------");
            FileUtils.writeLine(bufferedWriter, "echo dd if=/dev/zero of=/mytempfile");
            FileUtils.writeLine(bufferedWriter, "echo rm -f /mytempfile");
            FileUtils.writeLine(bufferedWriter, "echo shutdown now");
            FileUtils.writeLine(bufferedWriter, "echo ------");
            FileUtils.writeLine(bufferedWriter, "echo please ensure that you have run the above commands in the base image");
            FileUtils.writeLine(bufferedWriter, "read -n1 -rsp $'Press any key to continue or Ctrl+C to exit...\\n'");
            FileUtils.writeLine(bufferedWriter, "echo compressing base image");
            FileUtils.writeLine(bufferedWriter, "mv /home/vms/deb/debvm-full.qcow2 /home/vms/deb/debvm-full.qcow2_backup");
            FileUtils.writeLine(bufferedWriter, "qemu-img convert -O qcow2 -c /home/vms/deb/debvm-full.qcow2_backup /home/vms/deb/debvm-base.qcow2");
            FileUtils.writeLine(bufferedWriter, "echo your compressed image is ready at: /home/vms/deb/debvm-base.qcow2");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
