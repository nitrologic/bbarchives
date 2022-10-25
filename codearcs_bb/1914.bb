; ID: 1914
; Author: Vic 3 Babes
; Date: 2007-02-03 21:28:51
; Title: Xor Checksumming
; Description: Checksum a file to detect corruption tampering

;Use Xor Checksums to check for external file-corruption
;or tampering - much easier to checksum a prefs file than
;test the validity of every variable that it might hold
;when you reload it.
AppTitle "Xor Checksumming"
Graphics 400,400,0,2

Const RANDLOW=$1234, RANDHIGH=$56789ABC
Const SIZEOFBANK=2000

Global bank=CreateBank(SIZEOFBANK)

SetBuffer BackBuffer()
SeedRnd MilliSecs()

;fill example bank with random numbers
For offset=0 To SIZEOFBANK-4 Step 4
	PokeInt bank,offset,Rand($FFFFFFFF,$7FFFFFFF)
Next

;get a checksum from the bank - but ignore the last
;four bytes.

checksum=PeekInt(bank,0)
For offset=4 To SIZEOFBANK-8 Step 4
	checksum=checksum Xor PeekInt(bank,offset)
Next

;use the checksum as a random seed
SeedRnd checksum
;generate a random number from this seed
;in the given range
checksum=Rand(RANDLOW, RANDHIGH)
;poke it into the last 4 bytes of the bank
PokeInt bank,SIZEOFBANK-4,checksum
Text 0,0,"Random number from checksum seed: "+checksum

Text 0,30,"Press a key to see a demo of a seed bug"
Flip
WaitKey()

;Now when you load your bank back in later, you
;can simply reaquire the checksum with the second
;For loop above - use it for the seed with SeedRnd.
;Generate a random number between RANDLOW and RANDHIGH
;and compare it with the random number you stuck at
;the end of the bank - or wherever you stuck it.
;This method guards against tampering as well as
;file corruption.
;
;That's it for the algorithm - now a couple of things
;to be aware of:
;
;1.	you can use byte or short checksumming, but make
;	sure your bank is divisible by 2 or 4 if using
;	shorts/integers - or take it into account when
;	checksumming
;2.	Blitz2D and Blitz+ don't generate the same sequence
;	of random numbers from the same seeds.
;3.	If you game/program relies on random numbers, you
;	might want to preserve the seed with RndSeed or
;	randomize it again with SeedRnd - because if your
;	bank is always the same, then the same random
;	numbers will be generated after loading.
;4.	seed bug as at 01 Feb 07 - may have been fixed by
;	the time you read this.
;	Seeds of $FFFFFFFF or $7FFFFFFF always generate n1
;	in Rand(n1,n2)
Cls
Text 0,0,"Print 15 random numbers between 10 and 50"
SeedRnd $7FFFFFFF
For randnum=1 To 15
	Text 0,randnum Shl 4,Rand(10,50)
Next
Text 0,260,"Press a key to end"
;since one number is the highest positive integer
;and the other is the lowest, I guess it's an
;overflow thing
Flip
WaitKey
End
