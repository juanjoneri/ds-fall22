import socket

#Stack Overflow : https://stackoverflow.com/questions/18743962/python-send-udp-packet

from proto import MESSAGE;

UDP_IP = "127.0.0.1"
UDP_PORT = 2222
MESSAGE = "purchase sammy xbox 2"

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(bytes(MESSAGE, "utf-8"), (UDP_IP, UDP_PORT))