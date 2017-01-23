package main

import "time"
import "log"
import "net"
import "os"
import "fmt"

func runExperiment() int64 {

	messageSize := 10000
	msg := make([]byte, messageSize)
	i := 0
	for i < len(msg) {
		msg[i] = 'a'
		i += 1
	}

	ack := make([]byte, 100)

	start := time.Now()

	conn, err0 := net.Dial("tcp", "127.0.0.1:8082")
	defer conn.Close()

	if err0 != nil {
		fmt.Println(err0)
		os.Exit(1)
	}
	conn.Write(msg)

	n, _ := conn.Read(ack)
	s := string(ack[:n])
	if s == "Message received" {
		elapsed := time.Since(start).Nanoseconds()
		log.Printf("Took %d Microseconds", elapsed/1000)
		return elapsed / 1000
	} else {
		log.Println("Failed to receive ack")
		return 0
	}
}

func main() {
	var timeTrack int64
	var numExperiments = 10

	i := 0
	for i < numExperiments {
		timeTrack += runExperiment()
		i = i + 1
	}

	log.Printf("Took %d Microseconds, on average", timeTrack/int64(numExperiments))
}
