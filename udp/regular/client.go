package main

import "net"
import "os"
import "fmt"

func main() {
	a := make([]byte, 65500)

	i := 0
	for i < 10 {
		a[i] = 1
		i += 1
	}

	// fmt.Println(a)

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
