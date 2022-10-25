; ID: 43
; Author: Unknown
; Date: 2001-09-16 15:47:52
; Title: Pseudo Random Generator
; Description: ...and the numbers are reproduceable (is this a word?)

Global a = (pick a number here)
Global b = (pick another number here)
Global M = (pick another number here)

Global TEMP = (pick, euhm, just another number here)

; the numbers have to be whole numbers like 1 or 2 or 28375383 but no floating point or whatever like 1.2343

; M also equals the number of possible output numbers, since, at last, a divison is made: (a number ranging from 1 to M-1) / M. So you should choose M really large.

; You'd better try it first with the numbers you choose, to see if it doesn't repeat to early... Of course the list of numbers is repeated after a maximum of M outputs...

; euh.. code can be wrong.. doing this from the head and i have never programmed functions which returned a number befor in blitz

function Random()

TEMP = (a * TEMP + b) MOD M

return Random=TEMP / M
; by this I meant the returnvalue of Random() = TEMP / M

end function

; the fun part is, that if you reset TEMP to it's initual value, the whole series of random numbers will be repeated. Can be usefull if you have a game which builds levels on random, but you want the same levels everytime a game is played. I know you can seed the build-in randomizer, but I thougt it didn't give the same numbers at one runtime. At least QB didn't but this is Blitz...

example:

Rx are the output random values

a = 21
b = 3
m = 16
temp = 6

X1 = (21 *  6 + 3) mod 16 =  1     R1 =  1 / 16
X2 = (21 *  1 + 3) mod 16 =  8     R2 =  8 / 16
X3 = (21 *  8 + 3) mod 16 = 11     R3 = 11 / 16
X4 = (21 * 11 + 3) mod 16 = 10     R4 = 10 / 16

etc...

this time we will choose wrong numbers:

a = 24 (only this one's changed)
b =  3
M = 16
TEMP = 6

X1 = ... = 3
X2 = ... = 11
X3 = ... = 11
X4 = ... = 11

as you can see... if the number 11 was the last output (before divison), the number 11 will repeat... that's not fun so test it first!

