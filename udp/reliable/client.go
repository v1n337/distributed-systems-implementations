package main

import "net"
import "os"
import "fmt"

func main() {
	msg := make([]byte, 100)
	ack := make([]byte, 1)

	ServerAddr, err := net.ResolveUDPAddr("udp", "127.0.0.1:10001")
	LocalAddr, err := net.ResolveUDPAddr("udp", "127.0.0.1:0")

	AckAddr, err := net.ResolveUDPAddr("udp", "127.0.0.1:10002")

	conn, err := net.DialUDP("udp", LocalAddr, ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	conn.Write(msg)

	connAck, err := net.ListenUDP("udp", AckAddr)
	_, _, err = connAck.ReadFromUDP(ack)
	fmt.Println(ack)

	conn.Close()
}
