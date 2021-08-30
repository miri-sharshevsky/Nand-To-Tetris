// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

@KBD
D = M
@BLACK
D;JGT
@WHITE

(WHITE)
    @LOOPWHITE
    0;JMP

(BLACK)
    @LOOPBLACK
    0;JMP

(LOOPBLACK)
    @SCREEN
    D=A
    @i
    M=D
    (LOOPB)
    @i
    D=M

    @24575
    D=A-D

    @STOP
    D;JLT
   
    @i
    A = M
    M=-1
    @i
    M=M+1
    @LOOPB
    0;JMP




(LOOPWHITE)
    @SCREEN
    D=A
    @i
    M=D
    (LOOPW)
    @i
    D=M

    @24575
    D=A-D

    @STOP
    D;JLT
   
    @i
    A = M
    M=0
    @i
    M=M+1
    @LOOPW
    0;JMP

    
(STOP)
    @0
    0;JMP