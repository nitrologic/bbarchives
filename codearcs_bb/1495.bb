; ID: 1495
; Author: Wings
; Date: 2005-10-20 13:25:09
; Title: ListBoxSorter
; Description: Sort item gadgets in a list box.

;Gadget sorting rutine.
window=CreateWindow("Sorte List example",100,100,800,600,0,3)
list=CreateListBox(10,10,300,500,window)
button=CreateButton("Sort",400,300,50,30,window)


AddGadgetItem list,"Hey u fools"
AddGadgetItem list,"Nobody listens"
AddGadgetItem list,"Everybody cant do nothing"
AddGadgetItem list,"U never make it"
AddGadgetItem list,"Try sort this out"
AddGadgetItem list,"Hahaha fools"
AddGadgetItem list,"do u think u can sort me ?"
AddGadgetItem list,"Ohh nooo u never gona make it"
AddGadgetItem list,"A"
AddGadgetItem list,"ZZza"
AddGadgetItem list,"aavvV"



While Not quit=1
	e=WaitEvent()
	s=EventSource()
	d=EventData()

	If e=$803 And s=window Then quit=1

	If e=1025 And s=button
		sort=True
		
		While sort=True
			stall=stall+1
			If stall>9999 Then abort=Confirm("Vill du avbruta sorteringen ?")
			If abort Then Exit
			sort=False
		
			For g=0 To (CountGadgetItems( list )-2)

				a$=GadgetItemText(list,g)
				b$=GadgetItemText(list,g+1)
		
				l1=Len(a$)
				l2=Len(b$)
				lx=l1
		
				If l2<l1 Then lx=l2 

				win=0
				For i=1 To lx
					a1=Asc(Upper$(Mid$(a$,i,1)))
					b1=Asc(Upper$(Mid$(b$,i,1)))
	
					If a1=b1
						;Ah fortsätt. ingen träff.
					Else If a1>b1
						win=1
						
						Exit
					Else If a1<b1
						win=2
						Exit
					End If
				Next 

				If win=1
	
					ModifyGadgetItem list,g,b$
					ModifyGadgetItem list,g+1,a$
					sort=True
					
				End If

			Next		
		Wend
		
	End If


Wend
