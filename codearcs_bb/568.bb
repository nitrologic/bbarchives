; ID: 568
; Author: Panno
; Date: 2003-02-02 15:12:05
; Title: RC4 Komp. Algo.
; Description: De / Encoding Tool

Dim sbox(255) 
Dim keyh(255) 
Global key$ 
Global rc4$ 
Global re$ 
Global ec$ 
Global clear$ 
Global rc4k 
Global rc4cryp 
Key$="TEST KEY"  ; your key up to 255 bytes long 


clear$="SAFE YOUR DATA AND CODE WITH THE COOL RC4 ALGORYTMUS - (only private use, payment for commerzial) !" 
rc4init (key$) 
a$= rc4cryp$(clear$) 
Print a$ 
rc4init (key$) 
clear$= rc4enc$(a$) 
Print clear$ 
WaitKey() 
End 








Function  rc4init(key$)     ;__________________-- init sbox and keyh 
   keylen = Len(key$) 
      For i = 0 To 255 
         sbox(i) = i 
         kptr =kptr+1 
         keyh(i) = Asc(Mid$(key$,kptr,1)) 
         If kptr = keylen Then kptr = 0 
      Next 
      For i = 0 To 255 

         j = (j+sbox(i)+keyh(i)) 
         j= j Mod 256 
         swap = sbox(i) 
         sbox(i) = sbox(j) 
         sbox(j) = swap 
      Next 
       
i = rc4k(1) 

End Function 



Function rc4k(init)    ;__________________ 
Local x,y 

If initflag Then 
    x=0 
      y=0 
      Else 

         x =x+1 
         x =x Mod 256 
         y =y+(sbox(x)) 
         y =y Mod 256 
         swap =  sbox(x) 
         sbox(x) = sbox(y) 
         sbox(y) = swap 
         t = sbox(x)+sbox(y) 
         t =t Mod 256 
          
         rc4k =sbox(t) 
         Return rc4k 

End If 
End Function 

Function rc4cryp$(s$) ;___________________ cryp 

slen = Len(s$) 
For r = 1 To slen 
c = Asc(Mid$(s$,r,1)) 
k = rc4k(0) 

re$=re$+(Chr$(c Xor k)) 
Next 
rc4$= re$ 

Return rc4$ 
End Function 

Function rc4enc$ (s$) ;__________________-- decryp 
strl= Len(s$) 
For x = 1 To strl 
c = Asc(Mid$(s$,x,1)) 
k = rc4k(0) 
r$= r$+Chr$(c Xor k) 
Next 
Return r$ 
End Function
