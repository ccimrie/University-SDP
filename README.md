Group 4 SDP
=============

Sending commands:
  Sending commands is done through the Bluetooth communication class's method sendToRobot(int [])
  it takes an array of int, with size 4. The first integer in the array is the opcode
  (reference the Brick file to see what the opcodes should be) the next 3 integers are
  options that can be parsed for some commands (like for example speed and direction of
  movement for a movement command);
  
Receiving commands:
  Receiving commands from the Brick is done through the Bluetooth communication class's method
  receiveFromRobot() which returns array of 4 integers. You can program those from the Brick.
  
Communication tests:
  Currently communication tests pass, with 19.7 packets sent per second, 1000 packages sent 0
  packages lost.

Team:
Maithreyi Venkatesh
Marija Pinkute
Simona Petrova
Mona Paun
Jakov Smelkin
Bachir Djelmami-Hani
Alex Adams
Nikolay Bogoychev
