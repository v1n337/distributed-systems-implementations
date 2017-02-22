# Java FUSE NFS
Uses FUSE and gRPC to implement an NFS like interface 

## Requirements
* Gradle
* Java

## Run Commands

* Build System
```
gradle build
```

* Modify the app config if needed
```
./src/main/resources/app_config.json 
```

* Run Server
```
./build/bin/fuse-nfs-server -appConfigFile ./src/main/resources/app_config.json 
```

* Run Client
```
./build/bin/fuse-nfs-client -appConfigFile ./src/main/resources/app_config.json
```

The remote directory will now be mounted at the client mount point present in the app config
