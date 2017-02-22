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
import ru.serce.jnrfuse.NotImplemented;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class FuseNFSClient extends FuseStubFS
{
    private static final Logger log = Logger.getLogger(FuseNFSClient.class.getName());
    private static NFSFuseGrpc.NFSFuseBlockingStub grpcStub;

    public static void main(String[] args)
    {
        FuseNFSClient fuseStub = new FuseNFSClient();
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50051)
            .usePlaintext(true)
            .build();
        grpcStub = NFSFuseGrpc.newBlockingStub(channel);

        try
        {
            log.info("Mounting filesystem");
            fuseStub.mount(Paths.get("/tmp/mnt"), true, true);
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
    public int mkdir(String path, @mode_t long mode)
    {
        log.info("In Mkdir - client");

        MkDirRequestParams mkDirRequestParams =
            MkDirRequestParams
                .newBuilder()
                .setPath(path)
                .build();

        grpcStub.mkdir(mkDirRequestParams);

        return 0;
    }

    @Override
    public int mknod(String path, @mode_t long mode, @dev_t long rdev)
    {
        log.info("In mknod - client");

        if (path == null)
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
    public int create(String path, @mode_t long mode, FuseFileInfo fi)
    {

        log.info("In Create - client");

        if (path == null)
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

}
