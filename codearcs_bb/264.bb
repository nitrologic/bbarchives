; ID: 264
; Author: semar
; Date: 2002-03-11 08:43:47
; Title: Sound Card Test
; Description: Checks if there's a sound card working on a PC

First, provide a valid and small .WAV file on the same dir of your program (for example "test.wav");
then, try this test:

If LoadSound("test.wav") = 0 then
    ;NO SOUND CARD or SOUND PROBLEMS
    ;take the necessary action
    ;e.g., set a global sound flag to false and test it before loading/playing any sound/music
Endif
