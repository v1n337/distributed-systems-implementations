package main

import (
	"log"
	"time"

	"golang.org/x/net/context"
	"google.golang.org/grpc"
	pb "cs798/goserver"
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
	c := pb.NewTransferrerClient(conn)

	dataSize := 100
	data := make([]byte, dataSize)
	i := 0
	for i < dataSize {
		data[i] = 1
		i += 1
	}

	numTests := 100
	totalTime := 0
	i = numTests

	for i > 0 {
		start := time.Now()
		_, err = c.SendData(context.Background(), &pb.DataRequest{Data: data})
		if err != nil {
			log.Fatalf("could not greet: %v", err)
		}

		elapsed := time.Since(start).Nanoseconds()
		log.Println("Time elapsed (nanoseconds): ", elapsed)

		totalTime += int(elapsed)
		i--
	}

	log.Println("Average response time (nanoseconds): ", totalTime/numTests)

}
