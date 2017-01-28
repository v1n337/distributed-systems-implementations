package main

import "net"
import "os"
import "fmt"
import "time"

func ackReceived(ln *net.UDPConn) bool {

	expectedDataSize := 1
	tmp := make([]byte, expectedDataSize)

	ln.SetReadDeadline(time.Now().Add(time.Duration(2)*time.Second))
	n, _, err := ln.ReadFromUDP(tmp)

	if err != nil {
		fmt.Println(err)
		return false
	}

	if n == expectedDataSize {
		return true
	} else {
		return false
	}
}

func main() {

	dataSize := 1000000
	data := make([]byte, dataSize)

	i := 0
	for i < dataSize {
		data[i] = 1
		i += 1
	}

	ServerAddr, err := net.ResolveUDPAddr("udp", ":10001")
	ClientAddr, err := net.ResolveUDPAddr("udp", ":10002")

	conn, err := net.DialUDP("udp", nil, ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	ln, err := net.ListenUDP("udp", ClientAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	packetsToSend := 10
	for packetsToSend > 0 {
		for {
			conn.Write(data)

			if ackReceived(ln) {
				packetsToSend--
				fmt.Println("packetsToSend: ", packetsToSend)
				break
			}
			fmt.Println("missed ack. retrying")
		}
	}

	conn.Close()
}
