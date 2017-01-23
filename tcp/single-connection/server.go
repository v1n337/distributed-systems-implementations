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
	defer ln.Close()

	for {
		conn, err := ln.Accept()
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		go handleRequest(conn)
	}
}

func handleRequest(conn net.Conn) {
	numBytesExpected := 10000

	buf := make([]byte, numBytesExpected)

	n, err := conn.Read(buf)
	if err != nil {
		fmt.Println("Error reading:", err.Error())
	}

	var reply []byte
	if n == numBytesExpected {
		reply = []byte("Message received")
	} else {
		reply = []byte("Dropped packets")
	}

	conn.Write(reply)
	conn.Close()

	fmt.Println("Read ", n, " bytes")
}
