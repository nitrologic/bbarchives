; ID: 1264
; Author: fall_x
; Date: 2005-01-22 07:43:19
; Title: File Encryption/Decryption
; Description: Use this to encrypt/decrypt files.

; example :

key$="PutYourKeyHere"
EncryptFile("original.txt","encrypted.txt",key$)
DecryptFile("encrypted.txt","decrypted.txt",key$)

; functions :

Function EncryptFile(in$,out$,k$)
	filein = ReadFile(in$)
	fileout = writeFile(out$)
	s$=""
	while not eof(filein)
		s$=s$+chr(ReadByte( filein ))
	wend
	
	backwards=false
	for i%=1 to len (s$)
		c$=Mid(s$,i%,1)
		a%=asc(c$)
		j%=i%
		while j%>len(k$)
			j%=j%-len(k$)
		wend
		if backwards then j%=len(k$)-j%+1
		if j=1 then backwards=false
		if j=len(k) then backwards=true
		kc$=Mid(k$,j%,1)
		ka%=asc(kc$)
		enc%=ka%+a%
		
		writeshort(fileout,enc)
		
	next
	
	closefile filein
	closefile fileout
	
end function


function DecryptFile(in$,out$,k$)
	filein = ReadFile(in$)
	fileout = writeFile(out$)
	i=0
	backwards=false
	while not eof(filein)
		i=i+1
		;s$=s$+chr(ReadByte( filein ))
		enc%=readshort(filein)
		
		j%=i%
		while j%>len(k$)
			j%=j%-len(k$)
		wend
		if backwards then j%=len(k$)-j%+1
		if j=1 then backwards=false
		if j=len(k) then backwards=true
		kc$=Mid(k$,j%,1)
		ka%=asc(kc$)
		a%=enc%-ka%
		writebyte fileout,a%
	wend
	
	closefile filein
	closefile fileout

end function
