package main

import "net"
import "fmt"
import "os"

func main() {
	ack := []byte{1}

	ServerAddr, err := net.ResolveUDPAddr("udp", ":10001")
	AckAddr, err := net.ResolveUDPAddr("udp", "127.0.0.1:10002")

	conn, err := net.ListenUDP("udp", ServerAddr)

	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	msg := make([]byte, 100)

	for {
		_, _, err := conn.ReadFromUDP(msg)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}
		fmt.Println(msg)

		connClient, err := net.DialUDP("udp", nil, AckAddr)
		connClient.Write(ack)
	}
}
