package main

import "net"
import "os"
import "fmt"
import "encoding/binary"

func main() {

	dataSize := 8
	a := make([]byte, dataSize)

	i := 0
	for i < dataSize {
		a[i] = 1
		i += 1
	}

	ServerAddr, err := net.ResolveUDPAddr("udp", "127.0.0.1:10001")
	LocalAddr, err := net.ResolveUDPAddr("udp", "127.0.0.1:0")

	conn, err := net.DialUDP("udp", LocalAddr, ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	var j int64
	j = 0
	for {
		binary.LittleEndian.PutUint64(a, uint64(j))
		conn.Write(a)

		j++
	}

	conn.Close()
}
