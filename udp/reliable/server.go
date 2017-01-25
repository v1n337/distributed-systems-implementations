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

	numTests := 0
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
			numTests--
			if numTests == 0 {
				os.Exit(0)
			}
			j = int(data)
		}

		j++
	}
}
