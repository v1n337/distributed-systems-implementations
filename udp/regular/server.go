package main

import "net"
import "fmt"
import "os"

func main() {

	ServerAddr, err := net.ResolveUDPAddr("udp", ":10001")
	ln, err := net.ListenUDP("udp", ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	expectedDataSize := 100000
	tmp := make([]byte, expectedDataSize)

	for {
		n, _, err := ln.ReadFromUDP(tmp)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		fmt.Println(n)

		if n < expectedDataSize {
			fmt.Println(expectedDataSize -n, " packets dropped")
		}

	}
}
