package main

import "net"
import "os"
import "fmt"

func main() {

	dataSize := 65500
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

	conn.Write(a)

	conn.Close()
}
