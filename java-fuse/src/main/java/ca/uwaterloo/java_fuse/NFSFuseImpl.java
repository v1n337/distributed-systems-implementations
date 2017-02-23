package ca.uwaterloo.java_fuse;

import ca.uwaterloo.java_fuse.proto.*;
import org.apache.commons.io.FileUtils;
import ru.serce.jnrfuse.struct.FileStat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static java.nio.file.attribute.PosixFilePermission.*;


public class NFSFuseImpl extends NFSFuseGrpc.NFSFuseImplBase
{
    private static final Logger log = Logger.getLogger(NFSFuseImpl.class.getName());

    @Override
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
        responseBuilder.setUid(1000);
        responseBuilder.setGuid(1000);
        responseBuilder.setSize(file.length());

        GetAttrResponseParams getAttrResponseParams = responseBuilder.build();
        responseObserver.onNext(getAttrResponseParams);
        responseObserver.onCompleted();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void rmdir(ca.uwaterloo.java_fuse.proto.RmDirRequestParams request,
                      io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.VoidMessage> responseObserver)
    {
        log.info("In rmdir - server");
        try
        {
            Files.delete(Paths.get(request.getPath()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        responseObserver.onNext(VoidMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }

    public void open(ca.uwaterloo.java_fuse.proto.OpenRequestParams request,
                     io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.OpenResponseParams> responseObserver)
    {
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(new File(request.getPath()));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        OpenResponseParams openResponseParams = null;
        if (null != fileInputStream)
        {
            try
            {
                openResponseParams =
                    OpenResponseParams.newBuilder()
                        .setUid(1000)
                        .setFileHandle(fileInputStream.getFD().hashCode())
                        .build();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (null != openResponseParams)
        {
            responseObserver.onNext(openResponseParams);
            responseObserver.onCompleted();
        }
    }

    public void rename(ca.uwaterloo.java_fuse.proto.RenameRequestParams request,
                       io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.VoidMessage> responseObserver)
    {
        File file = new File(request.getOldPath());
        File newFile = new File(request.getNewPath());
        file.renameTo(newFile);

        responseObserver.onNext(VoidMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }


    public void read(ca.uwaterloo.java_fuse.proto.ReadRequestParams request,
                     io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.ReadResponseParams> responseObserver)
    {
        String text = null;
        try
        {
            text =
                Files.readAllLines(Paths.get(request.getPath()))
                    .stream().reduce((t, u) -> t + "\n" + u).get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        ReadResponseParams readResponseParams =
            ReadResponseParams.newBuilder().setText(text).build();

        responseObserver.onNext(readResponseParams);
        responseObserver.onCompleted();
    }

    public void write(ca.uwaterloo.java_fuse.proto.WriteRequestParams request,
                      io.grpc.stub.StreamObserver<ca.uwaterloo.java_fuse.proto.VoidMessage> responseObserver)
    {
        try
        {
            FileUtils.writeByteArrayToFile(new File(request.getPath()), request.getBytes().getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        responseObserver.onNext(VoidMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
