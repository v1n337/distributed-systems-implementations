package main

import "time"
import "log"
import "net"
import "os"
import "fmt"

func runExperiment() int64 {

	a := make([]byte, 1000000)

	start := time.Now()

	conn, err0 := net.Dial("tcp", "127.0.0.1:8082")
	if err0 != nil {
		fmt.Println(err0)
		os.Exit(1)
	}
	conn.Write(a)
	conn.Close()

	log.Printf("Took %d Microseconds", (time.Since(start).Nanoseconds())/1000)
	return time.Since(start).Nanoseconds()
}

func main() {
	var timeTrack int64
	i := 0
	for i < 10 {
		timeTrack += runExperiment()
		i = i + 1
	}

	log.Printf("Took %d Microseconds, on average", (timeTrack/10)/1000)
}
