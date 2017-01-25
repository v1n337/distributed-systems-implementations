package main

import "net"
import "fmt"
import "os"
import "encoding/binary"

func main() {

	ServerAddr, err := net.ResolveUDPAddr("udp", ":10001")
	ln, err := net.ListenUDP("udp", ServerAddr)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	expectedDataSize := 8
	tmp := make([]byte, expectedDataSize)

	j := 0
	for {
		_, _, err := ln.ReadFromUDP(tmp)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		data := int64(binary.LittleEndian.Uint64(tmp))

		if int(data) != j {
			fmt.Println("Packets dropped at ", j)
			os.Exit(0)
		}

		j++
	}
}
