package ca.uwaterloo.java_fuse;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class FuseNFSServer
{
    private static final Logger log = Logger.getLogger(FuseNFSServer.class.getName());
    private Server server;

    public static void main(String[] args)
        throws InterruptedException, IOException
    {
        final FuseNFSServer server = new FuseNFSServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start()
        throws IOException
    {
        int port = 50051;
        server =
            ServerBuilder.forPort(port)
                .addService(new NFSFuseImpl())
                .build()
                .start();
        log.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                FuseNFSServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop()
    {
        if (server != null)
        {
            server.shutdown();
        }
    }

    private void blockUntilShutdown()
        throws InterruptedException
    {
        if (server != null)
        {
            server.awaitTermination();
        }
    }

}
