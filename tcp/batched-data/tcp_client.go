package main

import "time"
import "log"
import "net"
import "os"
import "fmt"

func runExperiment() int64 {

	var i = 0

	start := time.Now()
	a := make([]byte, 1)
	for i < 100 {

		conn, err0 := net.Dial("tcp", "127.0.0.1:8082")
		if err0 != nil {
			fmt.Println(err0)
			os.Exit(1)
		}

		var j = 0
		for j < 10 {
			conn.Write(a)
			j += 1
		}

		conn.Close()
		i += 1
	}
	log.Printf("Took %d Microseconds", (time.Since(start).Nanoseconds())/1000)

	return time.Since(start).Nanoseconds()
}

func main() {
	var timeTrack int64
	i := 0
	for i < 10 {
		timeTrack += runExperiment()
		i += 1
	}

	log.Printf("Took %d Microseconds, on average", (timeTrack/10)/1000)
}
