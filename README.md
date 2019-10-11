# kvm-builder
A tool to quickly setup KVM environments on NCL. 

## What do I get from this?
- A ns file for use with DETERLab and startup scripts for each physical node
- Installation script for KVM
- Image creation and compression scripts (currently only for Debian 8)
- KVM network configurations for each physical node
- VM installation script for easy deployment after the environment has been setup

## How do I run this tool?
This tool can be run on any machine with JDK 8 (tested with 8u121) installed.

First, clone this repository. Then run the following command: 

For macOS/Linux:
```
$ ./gradlew start --console plain
```

For Windows:
```
> gradlew.bat start --console plain
```

### Sample Output
```
$ ./gradlew start --console plain
  :compileJava UP-TO-DATE
  :processResources UP-TO-DATE
  :classes UP-TO-DATE
  :start
  
  Creating root output directory/cleaning it out
  Setting KVM config
  Please enter in number of physical nodes (1-100): 5
  Number of physical nodes: 5
  Please enter in number of virtual nodes per physical node (1-20): 5
  Number of VMs per physical node: 5
  Creating files
  
  KVM
  node0: 172.30.1.2
  	debvm-1 - debvm-5: 192.168.101.2 - 192.168.101.6
  node1: 172.30.1.3
  	debvm-1 - debvm-5: 192.168.102.2 - 192.168.102.6
  node2: 172.30.1.4
  	debvm-1 - debvm-5: 192.168.103.2 - 192.168.103.6
  node3: 172.30.1.5
  	debvm-1 - debvm-5: 192.168.104.2 - 192.168.104.6
  node4: 172.30.1.6
  	debvm-1 - debvm-5: 192.168.105.2 - 192.168.105.6
  
  BUILD SUCCESSFUL
  
  Total time: 5.138 secs
```

### Sample Output Folder Structure
```
output
├── chmod.sh
└── kvm
    ├── 0-deterlab-ns-file
    │   ├── 1-import-ns-file-into-deterlab-you-can-also-change-the-topology
    │   ├── kvm.ns
    │   ├── node0-nodeStartup.sh
    │   ├── node1-nodeStartup.sh
    │   ├── node2-nodeStartup.sh
    │   ├── node3-nodeStartup.sh
    │   └── node4-nodeStartup.sh
    ├── 1-install-kvm
    │   └── KVM-install.sh
    ├── 2-create-image
    │   ├── 1-run-create-image-script-first
    │   ├── 2-then-run-compress-image-script
    │   ├── compress-image.sh
    │   └── create-image.sh
    ├── 3-create-network-and-routes
    │   ├── 1-run-the-corresponding-script-on-each-physical-node
    │   ├── node0-network.sh
    │   ├── node1-network.sh
    │   ├── node2-network.sh
    │   ├── node3-network.sh
    │   ├── node4-network.sh
    │   ├── xml-node0-network.xml
    │   ├── xml-node1-network.xml
    │   ├── xml-node2-network.xml
    │   ├── xml-node3-network.xml
    │   └── xml-node4-network.xml
    ├── 4-install-vm
    │   ├── 1-ensure-that-you-have-debvm-base.qcow2-in-this-folder
    │   ├── 2-run-this-script-file-on-all-physical-nodes
    │   └── VM-install.sh
    └── network-ip.txt
```

## Instructions for Deployment on NCL
1. Edit the variables `KVM_IMAGE_NAME` and `KVM_STARTUP_FILE_FOLDER` in [NSFile.java](src/main/java/kvm/NSFile.java) 
to use the correct image and user folder on NCL (this can be done with just a text editor). 
    - If you do not yet have a KVM image on NCL, start with the Ubuntu Server 14.04 image and use the `KVM-install.sh` 
    script in the `output/1-install-kvm` folder. 
2. Run the tool using the instructions above. 
3. Upload the resulting output files to your NCL user folder, then run the `chmod.sh` script to make all the files executable. 
    - The resulting path of the `0-deterlab-ns-file` folder holds the startup scripts and should either be copied to 
    where you defined the `KVM_STARTUP_FILE_FOLDER`, or the variable can just point to the `0-deterlab-ns-file` folder itself.
4. If you need to create the base VM image (currently of Debian 8), use the `create-image.sh` script in the `output/2-create-image` folder.
    - The script has been tested to run on an Ubuntu 14.04 machine (it can either be your own local machine or a node on NCL).
    - The script requires internet access and working `apt-get` sources. 
    - If you want to customize the VM image, you can do so now. 
    - After installation is completed, run these commands in the VM:
      ```
      $ dd if=/dev/zero of=/mytempfile
      $ rm -f /mytempfile
      $ shutdown now
      ```
    - After the VM has shutdown, run the `compress-image.sh` script to create a compressed copy of the VM image. 
    - The resulting compressed image can be found here: ` /home/vms/deb/debvm-base.qcow2`
5. Create an experiment on NCL using the `kvm.ns` file found in `output/0-deterlab-ns-file`.
    - You can change the network topology here, but do not change the IP addresses of the physical nodes because they are 
    tied to the network configurations for the virtual nodes. 
6. On each physical node, run the corresponding network script in `output/3-create-network-and-routes`. 
    - e.g. on `node0`, run the `node0-network.sh` script.
7. Before installing the VMs on each physical node, make sure that you have the base VM image `debvm-base.qcow2` in 
    the folder `output/4-install-vm`. 
8. On each physical node, run the `VM-install.sh` script in `output/4-install-vm`.
9. And that's it! You can find the IP addresses of each physical and virtual node in the `network-ip.txt` file in the `output` folder.

## Other Notes
- There is a limit of 300 physical nodes and 20 virtual nodes (per physical node) (this can of course be changed by editing MAX_PHYSICAL_NODES and MAX_VIRTUAL_NODES in [Main.java](src/main/java/Main.java)). 
