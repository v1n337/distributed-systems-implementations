package main

import (
	"log"
	"net"
	"time"
	"io"

	// "golang.org/x/net/context"
	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"
	pb "cs798/streaming-service"
)

const (
	port = ":50051"
)

type server struct{}

func (s *server) ReceiveDataChunks(stream pb.StreamingService_ReceiveDataChunksServer) error {

	startTime := time.Now()
	for {
		dataChunk, err := stream.Recv()
		if err == io.EOF {
			elapsed := time.Since(startTime).Nanoseconds()
			log.Println("Time elapsed: ", elapsed)
			return stream.SendAndClose(&pb.Empty{})
		}
		if err != nil {
			return err
		}
		log.Println("Bytes read: ", len(dataChunk.Data))
	}
}

func main() {
	lis, err := net.Listen("tcp", port)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}

	s := grpc.NewServer()
	pb.RegisterStreamingServiceServer(s, &server{})

	// Register reflection service on gRPC server.
	reflection.Register(s)
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
