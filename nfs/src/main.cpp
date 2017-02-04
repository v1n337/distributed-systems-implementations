#include <iostream>

int main(int argc, char* argv[])
{
	std::string sshfscmd ("sshfs ");
	std::string command = sshfscmd + argv[1] + " " + argv[2];
	std::cout << command << std::endl;
	const char * c = command.c_str();
	system(c);
}
