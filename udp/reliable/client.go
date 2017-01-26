package main

import "net"
import "os"
import "fmt"


func ackReceived(conn UDPConn) bool {
	n, _, err := ln.ReadFromUDP(tmp)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}

func main() {

	dataSize := 1
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

	ln, err := net.ListenUDP("udp", ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	bytesToSend := 10000
	for bytesToSend > 0 {
		for {
			conn.Write(data)

			if ackReceived(ln) {
				bytesToSend--
				break
			}
		}
	}

	conn.Close()
}
