package kvm;

import utils.FileUtils;

import java.nio.file.Paths;

public class KVM {

    // kvm directories
    protected static final String KVM_ROOT_FOLDER = "kvm/";
    protected static final String STEP_0_DETERLAB_FOLDER = "0-deterlab-ns-file/";
    protected static final String STEP_1_INSTALL_KVM_FOLDER = "1-install-kvm/";
    protected static final String STEP_2_CREATE_IMAGE_FOLDER = "2-create-image/";
    protected static final String STEP_3_CREATE_NETWORK_AND_ROUTES_FOLDER = "3-create-network-and-routes/";
    protected static final String STEP_4_INSTALL_VM_FOLDER = "4-install-vm/";

    public static void step0(String root, int physicalNodes) {
        NSFile.createNsFile(root, physicalNodes);
        NodeStartupScript.createNodeStartupScript(root, physicalNodes);
    }

    public static void step1(String root) {
        InstallKVM.createInstallScript(root);
    }

    public static void step2(String root) {
        CreateImageScript.createImageScript(root);
    }

    public static void step3(String root, int physicalNodes, int vmsPerNode) {
        // TODO: let user specify IP ranges (and check that ranges are valid)
        NetworkAndRoutes.createNetworkAndRoutes(root, physicalNodes, vmsPerNode);
    }

    public static void step4(String root, int physicalNodes, int vmsPerNode) {
        InstallVMScript.createInstallVMScript(root, physicalNodes, vmsPerNode);
    }

    public static void createKvmDirectories(String root) {
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(root + KVM_ROOT_FOLDER));
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(root + KVM_ROOT_FOLDER + STEP_0_DETERLAB_FOLDER));
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(root + KVM_ROOT_FOLDER + STEP_1_INSTALL_KVM_FOLDER));
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(root + KVM_ROOT_FOLDER + STEP_2_CREATE_IMAGE_FOLDER));
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(root + KVM_ROOT_FOLDER + STEP_3_CREATE_NETWORK_AND_ROUTES_FOLDER));
        FileUtils.checkAndCreateThenClearDirectory(Paths.get(root + KVM_ROOT_FOLDER + STEP_4_INSTALL_VM_FOLDER));
    }

}
