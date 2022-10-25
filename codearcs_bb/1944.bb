; ID: 1944
; Author: Matt Merkulov
; Date: 2007-03-13 12:37:52
; Title: Bank as dynamic aray
; Description: Snow fx example

;Bank as dynamic array (snow demo) by Matt Merkulov

;Maxrec - a maximum quantity of records, reclen - length of record in bytes
;In this example record contains coordinates:  x(float), y(float), increments: 
;dx(Float), dy(Float), grayscale color(byte) - 17 bytes)
Const maxrec = 120000, reclen = 17
;Creating bank
bnk = CreateBank(maxrec * reclen)
;Address of last element in bank
addr = -reclen

Graphics 800, 600, 32

;An array for storage of values of colors
Dim col(255)
For n = 0 To 255
 col(n) = n * 65793
Next

SetBuffer BackBuffer()
Repeat
;We add 400 snowflakes
 For n = 1 To 400
  addr = addr + reclen
  PokeFloat bnk, addr, Rand(800);coordinate x
  PokeFloat bnk, addr + 4, 0;coordinate y
  PokeFloat bnk, addr + 8, Rnd(-1, 1);an increment for x
  PokeFloat bnk, addr + 12, Rnd(2, 10);an increment for y
  PokeByte bnk, addr + 16, Rnd(64, 255);number of color
 Next

 Cls
 LockBuffer BackBuffer()

;Checking all snowflakes in bank
 n = 0
 While n <= addr
;Adding increments to coordinates
 x# = PeekFloat(bnk, n) + PeekFloat(bnk, n + 8)
 y# = PeekFloat(bnk, n + 4) + PeekFloat(bnk, n + 12)
 If x# < 0 Or x# > 799 Or y# > 599 Then
  ;If the snowflake has left screen area - deleting it from bank
  CopyBank bnk, addr, bnk, n, reclen
  addr = addr - reclen
  ;As the last entry has been moved on the place of current, so the address does not increase
 Else
  ;If it is still on the screen - updating coordinates
  WritePixelFast x#, y#, col(PeekByte(bnk, n + 12))
  PokeFloat bnk, n, x#
  PokeFloat bnk, n + 4, y#
  ;Drawing current snowflake
  n = n + reclen
 End If
 Wend

 UnlockBuffer BackBuffer()
 Flip

Until KeyHit (1)
