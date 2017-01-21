package main

import "net"
import "fmt"
import "os"

func main() {
	
	ln, err := net.Listen("tcp", ":8082")
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	for {

		conn, err := ln.Accept()
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}
		
		tmp := make([]byte, 1)
		_, err = conn.Read(tmp)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		conn.Write([]byte{'B'})

		fmt.Println(tmp)
	}
}
