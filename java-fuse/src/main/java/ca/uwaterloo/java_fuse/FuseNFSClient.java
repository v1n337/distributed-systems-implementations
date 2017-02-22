package ca.uwaterloo.java_fuse;

import ca.uwaterloo.java_fuse.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jnr.ffi.Pointer;
import jnr.ffi.types.dev_t;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class FuseNFSClient extends FuseStubFS
{
    private static final Logger log = Logger.getLogger(FuseNFSClient.class.getName());
    private static NFSFuseGrpc.NFSFuseBlockingStub grpcStub;

    public static void main(String[] args)
        throws Exception
    {
        Options.initializeInstance(args);

        FuseNFSClient fuseStub = new FuseNFSClient();
        AppConfig appConfig = Options.getInstance().getAppConfig();
        ManagedChannel channel =
            ManagedChannelBuilder
                .forAddress(appConfig.getServerAddress(), appConfig.getServerPort())
                .usePlaintext(true)
                .build();
        grpcStub = NFSFuseGrpc.newBlockingStub(channel);

        try
        {
            log.info("Mounting filesystem");
            fuseStub.mount(Paths.get(appConfig.getClientMountPoint()), true, false);
        }
        finally
        {
            fuseStub.umount();
            log.info("Unmounted filesystem");
        }
    }

    @Override
    public int getattr(String path, FileStat stat)
    {
        GetAttrRequestParams getAttrRequestParams =
            GetAttrRequestParams
                .newBuilder()
                .setPath(path)
                .build();

        GetAttrResponseParams getAttrResponseParams = grpcStub.getattr(getAttrRequestParams);

        stat.st_mode.set(getAttrResponseParams.getMode());
        stat.st_nlink.set(getAttrResponseParams.getNlink());
        stat.st_uid.set(getAttrResponseParams.getUid());
        stat.st_gid.set(getAttrResponseParams.getGuid());
        stat.st_size.set(getAttrResponseParams.getSize());

        return 0;
    }

    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, @off_t long offset, FuseFileInfo fi)
    {
        ReadDirRequestParams readDirRequestParams =
            ReadDirRequestParams
                .newBuilder()
                .setPath(path)
                .build();

        ReadDirResponseParams readDirResponseParams = grpcStub.readdir(readDirRequestParams);
        List<String> fileNames = readDirResponseParams.getFilenamesList();

        fileNames.forEach(
            fileName ->
            {
                filter.apply(buf, fileName, null, 0);
            }
        );

        return 0;
    }

    @Override
    public int open(String path, FuseFileInfo fi)
    {
        if (null == path)
        {
            return -ErrorCodes.ENOENT();
        }

        OpenRequestParams openRequestParams =
            OpenRequestParams
                .newBuilder()
                .setPath(path)
                .build();

        OpenResponseParams openResponseParams = grpcStub.open(openRequestParams);

        return 0;
    }

    @Override
    public int mkdir(String path, @mode_t long mode)
    {
        log.info("In Mkdir - client");

        if (path == null || Files.exists(Paths.get(path)))
        {
            return -ErrorCodes.EEXIST();
        }
        else
        {
            MkDirRequestParams mkDirRequestParams =
                MkDirRequestParams
                    .newBuilder()
                    .setPath(path)
                    .build();

            grpcStub.mkdir(mkDirRequestParams);
        }

        return 0;
    }

    @Override
    public int mknod(String path, @mode_t long mode, @dev_t long rdev)
    {
        log.info("In mknod - client");

        if (path == null || Files.exists(Paths.get(path)))
        {
            return -ErrorCodes.EEXIST();
        }
        else
        {
            CreateRequestParams createRequestParams =
                CreateRequestParams
                    .newBuilder()
                    .setPath(path)
                    .build();

            grpcStub.create(createRequestParams);
        }

        return 0;
    }

    @Override
    public int rmdir(String path)
    {
        if (path == null || !Files.exists(Paths.get(path)))
        {
            return -ErrorCodes.EEXIST();
        }
        else
        {
            RmDirRequestParams rmDirRequestParams =
                RmDirRequestParams
                    .newBuilder()
                    .setPath(path)
                    .build();

            grpcStub.rmdir(rmDirRequestParams);
        }

        return 0;
    }

    public int rename(String oldpath, String newpath)
    {

        if (oldpath == null || newpath == null)
        {
            return -ErrorCodes.EEXIST();
        }

        RenameRequestParams renameRequestParams =
            RenameRequestParams.newBuilder()
                .setOldPath(oldpath)
                .setNewPath(newpath)
                .build();

        grpcStub.rename(renameRequestParams);

        return 0;
    }

}
