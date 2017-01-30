package main

import (
	"log"

	"golang.org/x/net/context"
	"google.golang.org/grpc"
	pb "cs798/streaming-service"
)

const (
	address = "129.97.173.70:50051"
)

func main() {
	conn, err := grpc.Dial(address, grpc.WithInsecure())
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}

	defer conn.Close()
	c := pb.NewStreamingServiceClient(conn)

	packetsToSend := 100
	dataSize := 1000000
	data := make([]byte, dataSize)
	i := 0
	for i < dataSize {
		data[i] = 1
		i += 1
	}

	var dataChunks []*pb.DataChunk
	i = packetsToSend
	for i > 0 {
		dataChunks = append(dataChunks, &pb.DataChunk{Data: data})	
		i--	
	}

	stream, err := c.ReceiveDataChunks(context.Background())
	if err != nil {
		log.Fatalf("%v.ReceiveDataChunks(_) = _, %v", c, err)
	}
	
	for _, dataChunk := range dataChunks {
		if err := stream.Send(dataChunk); err != nil {
			log.Fatalf("%v.Send(%v) = %v", stream, dataChunk, err)
		}
	}
	_, err = stream.CloseAndRecv()
	if err != nil {
		log.Fatalf("%v.CloseAndRecv() got error %v, want %v", stream, err, nil)
	}
}
