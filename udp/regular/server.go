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

	tmp := make([]byte, 65500)

	for {
		n, _, err := ln.ReadFromUDP(tmp)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		fmt.Println(n)

		s := string(tmp[:])
		fmt.Println(s)
	}
}
