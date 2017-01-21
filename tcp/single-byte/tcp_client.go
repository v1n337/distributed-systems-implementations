package main

import "time"
import "log"
import "net"
import "os"
import "fmt"

func runExperiment() int64 {

	a := []byte{'A'}
	ack := make([]byte, 1)

	start := time.Now()

	conn, err0 := net.Dial("tcp", "127.0.0.1:8082")
	if err0 != nil {
		fmt.Println(err0)
		os.Exit(1)
	}
	conn.Write(a)

	_, err1 := conn.Read(ack)
	var elapsed time.Duration
	if ack[0] != 0 {
		elapsed = time.Since(start)
		fmt.Println(ack)
	}

	if err1 != nil {
		fmt.Println(err1)
		os.Exit(1)
	}

	conn.Close()

	return elapsed.Nanoseconds()
}

func main() {
	var timeTrack int64
	i := 0
	for i < 10 {
		timeTrack += runExperiment()
		i = i + 1
	}

	log.Printf("Took %d Microseconds, on average", (timeTrack / 10) / 1000)
}
