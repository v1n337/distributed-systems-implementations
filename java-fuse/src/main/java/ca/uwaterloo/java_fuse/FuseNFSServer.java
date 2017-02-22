package ca.uwaterloo.java_fuse;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.logging.Logger;

public class FuseNFSServer
{
    private static final Logger log = Logger.getLogger(FuseNFSServer.class.getName());
    private Server server;

    public static void main(String[] args)
        throws Exception
    {
        Options.initializeInstance(args);

        final FuseNFSServer server = new FuseNFSServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start()
        throws Exception
    {
        int port = Options.getInstance().getAppConfig().getServerPort();
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
                System.err.println("Shutting down gRPC server since JVM is shutting down");
                FuseNFSServer.this.stop();
                System.err.println("Server shut down");
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
