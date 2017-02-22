package ca.uwaterloo.java_fuse;

import ca.uwaterloo.java_fuse.proto.GetAttrResponseParams;
import ca.uwaterloo.java_fuse.proto.NFSFuseGrpc;
import ca.uwaterloo.java_fuse.proto.ReadDirResponseParams;
import ca.uwaterloo.java_fuse.proto.VoidMessage;
import jnr.posix.POSIX;
import ru.serce.jnrfuse.struct.FileStat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.attribute.PosixFilePermission.*;


public class NFSFuseImpl extends NFSFuseGrpc.NFSFuseImplBase
{
    private static final Logger log = Logger.getLogger(NFSFuseImpl.class.getName());

    public void getattr(ca.uwaterloo.java_fuse.proto.GetAttrRequestParams request,
                        io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.GetAttrResponseParams> responseObserver)
    {
        GetAttrResponseParams.Builder responseBuilder = GetAttrResponseParams.newBuilder();
        File file = new File(request.getPath());
        if (file.isDirectory())
        {
            responseBuilder.setMode(FileStat.S_IFDIR | 0755);
            responseBuilder.setNlink(2);
        }
        else if (file.isFile())
        {
            responseBuilder.setMode(FileStat.S_IFREG | 0777);
            responseBuilder.setNlink(1);
        }

        GetAttrResponseParams getAttrResponseParams = responseBuilder.build();
        responseObserver.onNext(getAttrResponseParams);
        responseObserver.onCompleted();
    }

    public void readdir(ca.uwaterloo.java_fuse.proto.ReadDirRequestParams request,
                        io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.ReadDirResponseParams> responseObserver)
    {
        ReadDirResponseParams.Builder responseBuilder = ReadDirResponseParams.newBuilder();

        File file = new File(request.getPath());
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (null != files)
            {
                List<File> fileList = Arrays.asList(files);

                List<String> fileNameList =
                    fileList.stream()
                        .filter(Objects::nonNull)
                        .map(File::getName)
                        .collect(Collectors.toList());

                responseBuilder.addAllFilenames(fileNameList);
            }
        }

        ReadDirResponseParams readDirResponseParams = responseBuilder.build();
        responseObserver.onNext(readDirResponseParams);
        responseObserver.onCompleted();
    }

    public void mkdir(ca.uwaterloo.java_fuse.proto.MkDirRequestParams request,
                      io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.VoidMessage> responseObserver)
    {
        log.info("In Mkdir server");

        Set<PosixFilePermission> perms = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ, GROUP_WRITE);
        FileAttribute fileAttribute = PosixFilePermissions.asFileAttribute(perms);

        Path path = Paths.get(request.getPath());
        if (!Files.exists(path))
        {
            try
            {
                Files.createDirectory(path, fileAttribute);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        responseObserver.onNext(VoidMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }

    public void create(ca.uwaterloo.java_fuse.proto.CreateRequestParams request,
                       io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.VoidMessage> responseObserver)
    {
        log.info("In Create - server");

        File file = new File(request.getPath());
        if (!file.exists())
        {
            try
            {
                if (file.createNewFile())
                {
                    log.info("File created");
                }
                else
                {
                    log.info("Unable to create file");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        responseObserver.onNext(VoidMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }

}
