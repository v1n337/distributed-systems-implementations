package main

import (
	"log"
	// "time"

	"golang.org/x/net/context"
	"google.golang.org/grpc"
	pb "cs798/streaming-service"
)

const (
	address = "localhost:50051"
)

func main() {
	conn, err := grpc.Dial(address, grpc.WithInsecure())
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}

	defer conn.Close()
	c := pb.NewStreamingServiceClient(conn)

	dataSize := 100
	data := make([]byte, dataSize)
	i := 0
	for i < dataSize {
		data[i] = 1
		i += 1
	}

	var dataChunks []*pb.DataChunk
	dataChunks = append(dataChunks, &pb.DataChunk{Data: data})

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

	// _, err = c.SendData(context.Background(), &pb.DataRequest{Data: data})
	// if err != nil {
	// 	log.Fatalf("could not send data: %v", err)
	// }

	// log.Println("Average response time (nanoseconds): ", totalTime/numTests)
}
