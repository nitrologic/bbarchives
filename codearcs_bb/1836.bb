; ID: 1836
; Author: kevin8084
; Date: 2006-10-08 17:21:52
; Title: Genetic Algorithm
; Description: Simple example of Genetic Algorithm

; **************************************************************************************************
; Critters by Kevin Lee Legge (kevin8084@gmail.com)
; **************************************************************************************************
;
; This program is an experiment in genetic algorithms that attempts to combine random chromosomes
; to create a perfect artificial life with these requirements:
; For Male:
; 1. The red color must be 255
; 2. The blue and green colors must be 0
; 3. The sex must be 0
;
; For Female:
; 1. The red and blue colors must be 0
; 2. The green color must be 255
; 3. the sex must be 255
;
; If the above requirements are met (either for male or female) then the solution is found.
;
; Description of chromosome used
;
; 0000 0000 0000 0000
;
; This is a 16-bit chromosome divided into 4-bit segments. Each 4-bit segment is a gene.
; The first segment is the red color segment
; The second segment is the green color segment
; The third segment is the blue color segment
; The fourth segment is the sex segment
; 
; Quick primer on binary numbers:
; The name "binary" comes from the fact that this is a base-2 number system. That is, there are
; only two numbers used in it. In this case, those two numbers are 1 and 0
; Binary numbers are read from the right to the left - the right being the lowest and the left
; the highest.
; The numbers below represent an 8-bit binary system. Each of the numbers are powers-of-2
; That is: 2^0=1 2^1=2 2^2=4 2^3=8 2^4=16 2^5=32 2^6=64 2^7=128
;
; 128  64  32  16  8  4  2  1
;
; The highest number that can be stored in an 8-bit number is 255. Why?
;  1  1  1  1  1 1 1 1 = 255
; 128+64+32+16+8+4+2+1 = 255
;
; If you have the binary number 01100101 can you figure out the decimal equivalent?
;
; 128 64 32 16 8 4 2 1
;  0  1  1  0  0 1 0 1 = 64+32+4+1 = 101 (that's one hundred one)
;
; The binary number 10110000 = ?
;
; 128 64 32 16 8 4 2 1
;  1  0  1  1  0 0 0 0 = 128+32+16 = 176
;
; **************************************
; Genetic Algorithm Theory in a nutshell
; **************************************
;
; First, let's start with a short story
;
; A long time ago there were creatures that looked like whales but lived on the land. They were happy
; creatures for thousands of years but then they noticed that food was getting a bit scarce. Well,
; that was ALMOST true. Food was still plentiful in the oceans. This didn't do the land whales any
; good, though, because they were afraid of the water. One day a baby land whale was born that rather
; ENJOYED the water. The baby, Billy, could only wade out a little way and splash happily while the 
; other land whales all looked on and shook their heads. When the baby grew up, he met a nice female 
; land whale, Suzie, who didn't mind his odd liking of the water. Time passed and the couple had a 
; cute, little baby, Joey. What was THIS? The new baby land whale ALSO liked the water. Though the 
; land whales didn't know it, the first land whale to like the water, Billy, did so because of a 
; mutated gene. This mutation was then passed on to his baby. Well, in another village there was 
; another baby land whale, Cindy, who had longer fingers and broader hands than any other land whales. 
; These would make swimming very easy if this land whale liked the water. Cindy did not, however. 
; Well, more time passed and one day Cindy and Joey met and fell in love. One thing led to another
; and before you knew it, they had a brand new addition to the family - Oscar. Oscar was a puzzle to
; all of the other land whales. He had long fingers, broad hands, AND he liked the water. He was very
; popular with the girl land whales who enjoyed watching him swimming with broad, powerful strokes.
; His unusual hands enabled him to speed through the water as fast as any fish.
;
; We could continue with the story but it is not necessary. The point is that if you combine
; a mutated chromosome with a normal one, the child of such a joining may have that mutated chromosome,
; and if you combine TWO mutated chromosomes, the children just may have both of the mutations.
;
; In genetic algorithm theory, you start off with a population of random chromosomes of a certain length.
; This length can be anything that you like. Each chromosome is divided into different segments, each
; of which describes a specific attribute. These divisions are the "genes".
; When you have your random population of chromosomes, you then check the "fitness" of each gene.
; The fitness is determined by how close the gene comes to solving the problem (of what you are
; looking for.)
; After you have calculated the fitness of all of the genes/chromosomes, you then select two of the
; fittest by using whatever selection process you want. In this case, we are going to use the Roulette
; Selection. Basically this is like playing roulette - wherever the ball lands is which chromosome we
; pick. The wheel is loaded in favor of the fittest chromosomes, however, which take the largest portion
; of the wheel.
; Now that we have our roulette-chosen chromosomes we need to recombine them to form new chromosomes.
; We do this by randomly picking a spot on the chromosome and then swapping all of the genes AFTER that
; point with each other. As an example, suppose we have these two chromosomes:
; 1101001001100101
; 0110100111000011
; and suppose we have randomly picked the number 7 as the swap point. The chromosomes, just prior to
; the swap look like this:
; 1101001 001100101
; 0110100 111000011
; Notice how they are split at the 8th bit - just after the swap point?
; After swapping the bits, they are now:
; 1101001 111000011
; 0110100 001100101
; 
; 1101001001100101 -> 1101001111000011
; 0110100111000011 -> 0110100001100101
; This is called "Crossover", which is simply recombining two chromosomes with each other.
; The rate of crossover is about 0.7. When you generate a random number and check it with
; this rate, if the random number is LESS than the crossover rate, then do the crossover. Otherwise,
; don't do it.
; Finally we mutate the chromosome. The mutation rate varies but it is between .1 and .001. I prefer to 
; use .001 as 1 in 1,000 is a more realistic mutation rate than 1 in 10.
; To mutate the chromosome you iterate through each bit and flip it from a 1 to 0 or 0 to 1 if a
; random number is less then the mutation rate.
; From here you repeat from the calculation of fitness routine until either a chromosome evolves that
; meets your requirements, or until the maximum number of generations is exceeded - in which case the
; solution was not found.

Graphics3D 800,600
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Const player_type=1
Const ground_type=2

Const POPULATION_SIZE	=100 ; we are going to create 100 chromosomes at a time
Const CROSSOVER_RATE#	=0.7 ; .7 is the normal crossover rate when recombining genes
Const MUTATION_RATE#	=0.01; we are going to use this for a 1 in 1000 rate
Const CHROMOSOME_LENGTH	=16  ; our chromosomes are going to be 16-bit binary numbers
Const GENE_LENGTH	=4   ; 4-bit segment lengths for each gene
Const MAX_GENERATIONS	=500 ; going to allow a maximum generation rate of 500

Global rCount,gCount,bCount	; red, green, and blue counter
Global sex					; 0=male,15=female
Global bFound = False       ; used as flag for perfect chromosome found
Global generations = 0      ; counter for how many generations program ran
Global tempGenerations = 0  ; same as above
Global maleCount,femaleCount; not needed
Global offspring1$,offspring2$ ; holds 16-bit chromosome children

Type chromosome
	Field bits$ 	; this holds the 16-bit binary string
	Field fitness#  ; holds the fitness number
End Type

; this is our main chromosome
Global Population.chromosome[POPULATION_SIZE]
; this is a temporary chromosome used to evolve children chromosomes
Global temp.chromosome[POPULATION_SIZE]

Global buffer=CreateBank(CHROMOSOME_LENGTH*4) ; holds decimal conversion of each gene

Global sphere=CreateSphere() ; our male entity's body
HideEntity sphere

Global cube=CreateCube()     ; our female entity's body
HideEntity cube

Global camera=CreateCamera()
CameraRange camera,.1,500
CameraZoom camera,1.6
PositionEntity camera,0,1,0
EntityRadius camera,1
EntityType camera,player_type


Global light=CreateLight()

Global ground=CreatePlane()
PositionEntity ground,0,0,0
EntityColor ground,100,50,10
EntityType ground,ground_type

Collisions player_type,ground_type,2,2

createChromosomes() ; See if the monster lives

MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
HidePointer

While Not KeyHit(1)
If KeyDown(200) Then MoveEntity camera,0,0,1
If KeyDown(208) Then MoveEntity camera,0,0,-1
If KeyDown(203) Then MoveEntity camera,-1,0,0
If KeyDown(205) Then MoveEntity camera,1,0,0

TranslateEntity camera,0,-1,0
mxs#=-MouseXSpeed()/4.0
mys#=MouseYSpeed()/4.0
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
TurnEntity camera,mys#,mxs#,0
RotateEntity camera,EntityPitch#(camera),EntityYaw#(camera),0
If EntityPitch#(camera)>69 Then RotateEntity camera,69,0,0
If EntityPitch#(camera)<-69 Then RotateEntity camera,-69,0,0


UpdateWorld
RenderWorld
Text 0,0,"Looking for perfect red male or perfect green female..."
If Not bFound Then
	Text 0,10,"No solution found in "+(tempGenerations-1)+" generations."
Else
	Text 0,10,"Solution found in "+(tempGenerations-1)+" generations."
End If

Flip
Delay 10
Wend
FreeBank buffer
End


; **************************************************************************************************
;                                           FUNCTIONS
; **************************************************************************************************

Function createChromosomes()
; this function initializes the chromosomes
While Not bFound ; while perfect chromosome is not found
generations = generations+1 ; increment the generation counter
tempGenerations = generations
If generations > MAX_GENERATIONS Then Return ; if we exceed the maximum generation count, then return
totalFitness#=0.0
For i=0 To POPULATION_SIZE-1 ; POPULATION_SIZE is the size of the population
	Population[i]=New chromosome ; create a new chromosome
	Population[i]\bits$=randomBits(CHROMOSOME_LENGTH) ; get random bits for the chromosome
	parseBits(Population[i]\bits$,buffer)         ; parse the bits - translate from binary to decimal
	Population[i]\fitness#=calculateFitness()     ; get the fitness count for the chromosome
	If Population[i]\fitness#=999.0 Then bFound = True:Return ; we found the perfect chromosome
	
	totalFitness#=totalFitness#+Population[i]\fitness#    ; accumulative count of fitness
Next
 
nPop=0 ; population counter
While nPop < POPULATION_SIZE
temp[nPop]=New chromosome ; create a new chromosome
offspring1$=rouletteSelection(totalFitness)  ; select the fittest chromosome
offspring2$=rouletteSelection(totalFitness)  ; again, select the fittest chromosome
CrossOver(offspring1$,offspring2$)           ; recombine the two fittest chromosomes
Mutate(offspring1$)                          ; mutate
Mutate(offspring2$)                          ; mutate
temp[nPop]\bits$=offspring1$                 ; store offspring1
temp[nPop]\fitness#=0.0                      ; zero out the chromosome's fitness value
nPop = nPop+1                                ; increment the counter
temp[nPop]=New Chromosome                    ; create a new chromosome
temp[nPop]\bits$=offspring2$                 ; store offspring2
temp[nPop]\fitness#=0.0                      ; zero out the chromosome's fitness value
nPop = nPop+1                                ; increment the counter
Wend
For i=0 To POPULATION_SIZE-1
	Population[i] = temp[i]                  ; store temp in Population
	parseBits(Population[i]\bits$,buffer)    ; parse the bits - translate from binary to decimal
	Population[i]\fitness#=calculateFitness(); get the fitness count for the chromosome
	If Population[i]\fitness#=999.0 Then bFound=True:Return
	totalFitness#=totalFitness#+Population[i]\fitness#
Next

nPop=0 ; population counter
While nPop < POPULATION_SIZE
temp[nPop]=New chromosome ; create a new chromosome
offspring1$=rouletteSelection(totalFitness)  ; select the fittest chromosome
offspring2$=rouletteSelection(totalFitness)  ; again, select the fittest chromosome
CrossOver(offspring1$,offspring2$)           ; recombine the two fittest chromosomes
Mutate(offspring1$)                          ; mutate
Mutate(offspring2$)                          ; mutate
temp[nPop]\bits$=offspring1$                 ; store offspring1
temp[nPop]\fitness#=0.0                      ; zero out the chromosome's fitness value
nPop = nPop+1                                ; increment the counter
temp[nPop]=New Chromosome                    ; create a new chromosome
temp[nPop]\bits$=offspring2$                 ; store offspring2
temp[nPop]\fitness#=0.0                      ; zero out the chromosome's fitness value
nPop = nPop+1                                ; increment the counter
Wend

Wend
End Function


Function randomBits$(length)
; returns a string of random bits of length "length"
For i=1 To length
	If Rnd(0,1) > .5 Then ; if random number is greater than .5 then
		tempString$=tempString$+"1" ; add a 1 to the string
	Else
		tempString$=tempString$+"0" ; else add a 0 to the string
	End If
Next
Return tempString$
End Function

Function binary2Decimal%(bits$)
; this function converts the binary string to its decimal equivalent
val=0
value_added=1
For i=Len(bits$) To 1 Step-1
	If Mid$(bits$,i,1)="1" Then
		val=val+value_added
	End If
	value_added = value_added*2
Next
Return val
End Function

Function parseBits%(bits$,buf)
; this function parses the bits and checks for red, green, blue, and sex component values
cBuf=0
this_gene=0
bRed=True ; first number in bank is red component
bGreen=False ; second number in bank is green component
bBlue=False  ; third number in bank is blue component
bSex=False   ; fourth number in bank is sex component
For i=1 To CHROMOSOME_LENGTH Step GENE_LENGTH
	bogus=False
	this_gene=binary2Decimal(Mid$(bits$,i,GENE_LENGTH)) ; convert the chromosome string to its decimal values
	If bRed=True Then ; are we on the red component?
		PokeInt buf,cBuf,this_gene ; yes, then poke the value into the bank
		cBuf=cBuf+4 ; increment counter by 4 - because we are poking ints
		bRed=False  ; set flag to false because we are done checking the red component
		bGreen=True ; set to true because next component to check is green
		bogus=1     ; this emulates the "continue" statement of other languages
	End If
	If bGreen = True And bogus=False Then ; if we are on the green component and not midway through the loop
		PokeInt buf,cBuf,this_gene        ; then poke the green value into the bank
		cBuf=cBuf+4                       ; increment the counter for next position in the bank
		bogus=1                           ; continue from beginning of "For" statement
		bGreen=False                      ; we are done with green
		bBlue=True                        ; next we check the blue component
	End If
	If bBlue=True And bogus=False Then    ; if blue component and not midway through the loop then
		PokeInt buf,cBuf,this_gene        ; poke the blue value into the bank
		cBuf=cBuf+4                       ; increment the counter for next position in the bank
		bogus=1                           ; continue from beginning of loop
		bBlue=False                       ; we are done with blue
		bSex=True                         ; next we check the sex component
	End If
	If bSex=True And bogus=False Then     ; if sex component and not midway through the loop then
		PokeInt buf,cBuf,this_gene        ; poke sex component into the bank
		bogus=1                           ; continue from beginning of loop
		bSex=False                        ; we are done with sex
		bRed=True						  ; next we check the red component again
	End If
Next
Return Int(cBuf/4)                        ; return the number of elements. It is divided by 4
					                      ; because we incremented by 4 to hold int values in bank
End Function

Function calculateFitness#()
; this function calculates how close the value is to the target we set.
tempRed#=PeekInt(buffer,0)*17 ; get red component. Since each component uses 4 bits, then max
		                      ; value is 15. 15*17 = 255, which is max value of color component
tempGreen#=PeekInt(buffer,4)*17; get green component

tempBlue#=PeekInt(buffer,8)*17; get blue component

tempSex#=PeekInt(buffer,12)*17; get sex component

result#=0.0 ; this is the important variable. It is used to tell the program how close the calculated
            ; value is to what is wanted
If tempRed#=200 And tempSex=100 Then
	result#= 5 ; not a bad result, but not what we are looking for
ElseIf tempRed#=255 And tempGreen#=0 And tempBlue#=0 And tempSex=0 Then
	male=CopyEntity(sphere)
	EntityColor male,tempRed#,tempGreen#,tempBlue#
	PositionEntity male,Rand(-50,50),1,Rand(50)
	maleCount=maleCount+1
	PointEntity camera,male
	result#= 999; now THIS is what we are looking for. A pure red male
ElseIf tempGreen#=200 And tempSex=100 Then
	result#= 5
ElseIf tempGreen=255 And tempRed#=0 And tempBlue#=0 And tempSex=255 Then
	female=CopyEntity(cube)
	EntityColor female,tempRed#,tempGreen#,tempBlue#
	PositionEntity female,Rand(-50,50),1,Rand(50)
	femaleCount=femaleCount+1
	PointEntity camera,female
	result#= 999; now THIS is what we are looking for. A pure green female
Else
	result#=0  ; not a good result. We weren't very happy with the findings
End If
Return result# ; tell the calling function how happy/unhappy we were with the results
End Function

Function rouletteSelection$(totalFitness#)
; the chromosome with the fittest value has the best chance of being returned from this function
portion#=Rnd(0,1)*totalFitness#
tempFitness#=0.0
For i=0 To POPULATION_SIZE-1
	tempFitness#=tempFitness#+Population[i]\fitness#
	If tempFitness#>portion# Then
		Return Population[i]\bits$
	End If
Next
Return "" ; we didn't find anything worth returning
End Function

Function Crossover(child1$,child2$)
; this function takes two chromosomes and recombines them into new chromosomes
If Rnd(0,1) < CROSSOVER_RATE# Then 
	crossover=Int(Rnd(0,1)*CHROMOSOME_LENGTH)
	If crossover = 0 Then crossover = 1 ; we want at least ONE combining gene
	; the next two statements takes the genes from crossover to CHROMOSOME_LENGTH and
	; swaps them with each other. Thus, each chromosome is recombined into a new
	; configuration
	c1$=Mid$(child1$,1,crossover)+Mid$(child2$,crossover+1,CHROMOSOME_LENGTH)
	c2$=Mid$(child2$,1,crossover)+Mid$(child1$,crossover+1,CHROMOSOME_LENGTH)
	offspring1$=c1$
	offspring2$=c2$
End If
End Function

Function Mutate(bits$)
; this function mutates the chromosomes
For i=1 To Len(bits$)
	If Rnd(0,1) < MUTATION_RATE# Then
		bits$ = flipBits(bits$,i)
	End If
Next
End Function

Function flipBits$(bits$,where)
; this function changes a "1" to a "0" and a "0" to a "1"
tempString$=""

For i=1 To Len(bits$)
	If i=where Then
		If Mid$(bits$,i,1)="1" Then
			tempString$=tempString$+"0"
		Else
			tempString$=tempString$+"1"
		End If
	Else
		tempString$=tempString$+Mid$(bits$,i,1)
	End If
Next
Return tempString$
End Function
