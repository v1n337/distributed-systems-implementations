package main

import "net"
import "fmt"
import "os"

func main() {

	ServerAddr, err := net.ResolveUDPAddr("udp", ":10001")
	ClientAddr, err := net.ResolveUDPAddr("udp", ":10002")

	ln, err := net.ListenUDP("udp", ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	conn, err := net.DialUDP("udp", nil, ClientAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	expectedDataSize := 1000000
	tmp := make([]byte, expectedDataSize)
	ack := []byte {'1'}

	for {
		n, _, err := ln.ReadFromUDP(tmp)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		if n == expectedDataSize {
			conn.Write(ack)
		}
	}
}
