; ID: 2716
; Author: MCP
; Date: 2010-05-16 09:47:50
; Title: EncodeZip - Distribute zipped .decls &amp; .dlls via code archives.
; Description: Distribute zipped .decls & .dlls via code archives.

;*** EncodeZip.bb
;*** Written by MCP 2010
;
;*** Encodes your .zip file into a bb file that can be downloaded from a webpage
;*** just like regular bb source-code and then be compiled & run in either Blitz3D or
;*** BlitzPlus to reproduce the original binary .zip file.
;
;*** NB:- This software is only intended to be used for small zip files < 100k which may be
;***      used for porting decls/dll files + examples!!!!

;NB:- "sample.zip" will be encoded as "sample.zip.bb" which can then be sent to code archives via Blitz website.


Global filename$="sample.zip"		;<-- insert your own filename here and compile & run to produce encoded bb file

Graphics 640,480,0,2
SetBuffer BackBuffer()
Text 0,0,"Encoding bb file......"
Flip()
fp_in_size%=FileSize(filename$)
fp_in%=ReadFile(filename$)
fp_out%=WriteFile(filename$+".bb")
Restore decoder
Read dec_hdr_size%
nlines%=dec_hdr_size/(31*4)
tlines%=dec_hdr_size-(nlines*(31*4))
While nlines
	Read h$
	For i=0 To 30
		b%=HexToNumber(Mid$(h$,(i*8)+1,8))
		WriteInt fp_out,b
	Next
	nlines=nlines-1
Wend
If tlines
	Read h$
	l=Len(h$)
	i=1
	While l>=8
		b=HexToNumber(Mid$(h$,i,8))
		WriteInt fp_out,b
		i=i+8 : l=l-8
	Wend
	While l
		b=HexToNumber(Mid$(h$,i,2))
		WriteByte fp_out,b
		i=i+2 : l=l-2
	Wend
EndIf
myWriteString(fp_out,".zip_dat")
myWriteString(fp_out,"Data "+Chr$(34)+filename$+Chr$(34))
EncodeFile(fp_in,fp_out,fp_in_size)

CloseFile fp_in
CloseFile fp_out
Text 0,20,"Done - Press any key to exit."
Flip()
WaitKey()
End

Function EncodeFile(fp_in%,fp_out%,size%)
	Local i%,b%,h$,r$,lh%
	Local nlines%=size/(31*4)

	If nlines*(31*4)<size
		nlines=nlines+1
	EndIf
	myWriteString(fp_out,"Data ",0)
	myWriteString(fp_out,"$"+Hex$(size))
	While nlines
		myWriteString(fp_out,"Data "+Chr$(34),0)
		h$=""
		For i=0 To 30
			If size>=4
				b=ReadInt(fp_in)
				h$=h$+Hex$(b)
				size=size-4
			Else
				If size=0
					i=31
				Else
					If size<4
						While size
							b=ReadByte(fp_in)
							h$=h$+Mid$(Hex$(b),7,2)
							size=size-1
						Wend
						i=31
					EndIf
				EndIf
			EndIf
		Next
		myWriteString(fp_out,h$+Chr$(34))
		nlines=nlines-1
	Wend
	myWriteString(fp_out,"")
End Function

Function myWriteString(stream%,t$,newline%=1)
	Local c%,i%
	For i=1 To Len(t$)
		c=Asc(Mid$(t$,i,1))
		WriteByte stream,c
	Next
	If newline
		WriteByte stream,13
		WriteByte stream,10
	EndIf
End Function

Function HexToNumber%(t$)
	Local i
	Local a$
	Local na,n
	t$=Upper$(t$)
	For i=1 To Len(t$)
		a$=Mid$(t$,i,1)
		na=Asc(a$)
		If na>64
			na=na-55
		Else
			na=na-48
		EndIf
		n=(n Shl 4) Or na
	Next
	Return n
End Function

;*** WARNING - DO NOT MODIFY THE DATA BELOW!!!

.decoder
Data $00000450
Data "2A2A2A3B636544205A65646F622E70693B0A0D62202A2A2A74697257206E65744D207962322050430D3031300A0D3B0A2A2A2A3B6D6F4320656C69705220262074206E7520736968676F7270206D617272206F746E6F636575727473792074632072756F616E69622E2079722070697A656C69663B0A0D73202A2A2A"
Data "74206E697320656820656D6165726964726F7463687420796620736920656C6920736177657661732E2E2E640A0D0A0D7061724773636968303436203038342C322C302C65530A0D66754274207265666B636142666675422928726565540A0D30207478222C302C6F636544676E69646E696220207972612070697A"
Data "656C69662E2E2E2E460A0D222870696C0D0A0D297365520A65726F7470697A207461645F65520A0D7A2064616E5F706924656D6165520A0D7A206461735F706925657A6970660A0D72573D254665746928656C695F70697A656D616E0A0D29246E696C6E3D2573655F70697A657A69733133282F0D29342A696C740A"
Data "2573656E70697A3D7A69735F6E282D65656E696C33282A7329342A31570A0D29656C6968696C6E200D73656E6552090A68206461090A0D2420726F4620303D6933206F54090A0D303D256209547865486D754E6F287265622464694D2C246828382A69282C312B290D2929385709090A6574697220746E49622C7066"
Data "4E090A0D0D7478656C6E090A73656E69696C6E3D2D73656E570A0D310D646E652066490A6E696C740A0D736561655209246820646C090A0D6E654C3D2924682869090A0D0A0D313D696857096C20656C0D383D3E6209090A7865483D754E6F547265626D64694D2824682824382C692C0A0D29297257090949657469"
Data "6620746E0D622C706909090A382B693D6C203A20382D6C3D57090A0D0D646E656857090A20656C69090A0D6C483D62096F547865626D754E4D28726528246469692C24682929322C09090A0D7469725774794265706620650A0D622C3D69090920322B693D6C203A0D322D6C6557090A0A0D646E49646E45430A0D66"
Data "65736F6C656C69460D7066207865540A2C302074222C3032206C6C41656E6F64202D202120796E412079656B71206F742E746975460A0D222870696C570A0D294B746961292879656E450A0D0D0A0D646E75460A6F6974636548206E4E6F547865626D75742825720A0D2924636F4C0969206C6124612C2525616E2C"
Data "0D256E2C2474090A7070553D282472650D2924746F46090A3D6920726F5420316E654C202924742809090A0D4D3D246128246469692C24740D29312C6E09090A73413D6124612863090A0D2920664909363E616E090A0D34616E09092D616E3D0A0D35356C4509090A0D65736E090909616E3D610D38342D4509090A"
Data "6649646E09090A0D6E283D6E6C685320202934206E20724F090A0D617478654E52090A0D727574650D6E206E646E450A6E7546206F6974630D0A0D6E2A2A3B0A4157202A4E494E52202D20474E204F444D20544F4649444F485420594144204542204154574F4C45212121210A0D0A0D"
