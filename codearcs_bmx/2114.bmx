; ID: 2114
; Author: xlsior
; Date: 2007-09-30 18:16:25
; Title: CPU &amp; Video System Information
; Description: Detect CPU and ATI Video adapter information

' Registry routines by gman
' keys by xlsior

Framework BRL.StandardIO
Import BRL.Bank
Import BRL.Retro 

Import "-ladvapi32" 
'Private 
Extern "win32" 
   Function RegOpenKey:Int(hKeyParent:Int,SubKey$z,phkResult:Byte Ptr)="RegOpenKeyA@12" 
   Function RegCloseKey:Int(hKey:Int)="RegCloseKey@4" 
   Function RegEnumKey:Int(hKey:Int,idx:Int,Key:Byte Ptr,size:Int)="RegEnumKeyA@16" 
   Function RegEnumValue:Int(hKey:Int,idx:Int,ValueName:Byte Ptr,NameSize:Byte Ptr,Reserved:Int,nType:Byte Ptr,ValueBytes:Byte Ptr,ValueSize:Byte Ptr)="RegEnumValueA@32" 
   Function RegQueryValueEx:Int(hKey:Int,ValueName$z,Reserved:Int,nType:Byte Ptr,ValueBytes:Byte Ptr,ValueSize:Byte Ptr)="RegQueryValueExA@24" 
EndExtern 
'Public 

Const HKEY_CLASSES_ROOT:Int      = -2147483648 
Const HKEY_CURRENT_USER:Int      = -2147483647 
Const HKEY_LOCAL_MACHINE:Int   = -2147483646 
Const HKEY_USERS:Int         = -2147483645 
Const REG_ERROR_SUCCESS:Int    = 0 
Const REG_ERROR_EOF:Int         = 259     ' no more entries in key 
Const REG_SZ:Int            = 1      ' Data String 
Const REG_BINARY:Int         = 3      ' Binary Data in any form. 
Const REG_DWORD:Int            = 4    ' A 32-bit number. 
' Temporary Variables:
temp:Int=0
maxcpu:Int=0


' Cycle through to find how many CPU cores are present in the system:
For temp=0 To 1024
	If reg_iskey(HKEY_LOCAL_MACHINE,"hardware\description\system\centralprocessor\"+temp)=False Then
		maxcpu=temp
		Exit	' We found how many CPU's/Cores are present, so exit the loop
	End If
Next

Print ""
Print "Number of CPUs/cores: "+(maxcpu)
Print "CPU Speed in MHz    : "+reg_getvalue(HKEY_LOCAL_MACHINE,"hardware\description\system\centralprocessor\0\","~~MHz","none",types:Int=True) 
Print "CPU Name String     : "+reg_getvalue(HKEY_LOCAL_MACHINE,"hardware\description\system\centralprocessor\0\","processornamestring","none",types:Int=True) 
Print "CPU Identifier      : "+reg_getvalue(HKEY_LOCAL_MACHINE,"hardware\description\system\centralprocessor\0\","identifier","none",types:Int=True) 
Print "Vendor Identifier   : "+reg_getvalue(HKEY_LOCAL_MACHINE,"hardware\description\system\centralprocessor\0\","vendoridentifier","none",types:Int=True) 
Print ""

If reg_iskey(HKEY_LOCAL_MACHINE,"software\ATI Technologies\CDS\0000\0")=True Then 
	Print "ATI Video Adapter   : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\Driver\","Description","unknown",types:Int=True) 
	Print "Board Manufacturer  : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\ASIC\","Graphics Card Manufacturer","unknown",types:Int=True) 
	Print "ATI Device ID       : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\PCI Config\","Device ID","unknown",types:Int=True) 
	Print "ATI BIOS Date       : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\BIOS\","Date","unknown",types:Int=True) 
	Print "ATI Chip ID         : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\ASIC\","chip ID","unknown",types:Int=True) 
	Print "ATI 2D Driver       : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\Driver\","2D Driver Version","unknown",types:Int=True) 
	Print "ATI D3D Driver      : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\Driver\","D3D Driver Version","unknown",types:Int=True) 
	Print "ATI OGL Driver      : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\Driver\","OGL Driver Version","unknown",types:Int=True) 
	Print "ATI Bus Type        : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\ASIC\","Bus Type","unknown",types:Int=True) 
	Print "ATI Memory Size     : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\Memory\","size","unknown",types:Int=True) 
	Print "ATI Memory Type     : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\0000\0\Memory\","type","unknown",types:Int=True) 
	Print "DirectX Version     : "+reg_getvalue(HKEY_LOCAL_MACHINE,"SOFTWARE\ATI Technologies\CDS\system\0\","directx version","unknown",types:Int=True) 

End If 

Global reg_lasterr:Int      = REG_ERROR_SUCCESS 
Function reg_enumvalues:String(RegKey:Int,SubKey:String,delim:String="|",types:Int=False) 
   Local cRetVal:String="",key:String="",val:String="" 
   Local keybank:TBank=CreateBank(100),keybanksize:TBank=CreateBank(4),valbank:TBank=CreateBank(100),valbanksize:TBank=CreateBank(4),typebank:TBank=CreateBank(4) 
   Local char:Int=0,nIdx:Int=0,nType:Int=0 

   ' open the key 
   Local hKey:Int=reg_openkey(RegKey,SubKey:String) 
   If hKey<>-1    
    
      ' read in the values 
      Repeat 
         ' init the banks 
         PokeInt(typebank,0,0) 
         PokeInt(valbanksize,0,100) 
         PokeInt(keybanksize,0,100) 
      
         ' clear out the temp values 
         key:String="" 
         val:String="" 
          
         If RegEnumValue(hKey,nIdx,BankBuf(keybank),BankBuf(keybanksize),0,BankBuf(typebank),BankBuf(valbank),BankBuf(valbanksize))<>REG_ERROR_EOF 
            nType=PeekInt(typebank,0) 
            
            ' tack on the delimiter 
            If cRetVal:String<>"" 
               cRetVal:String=cRetVal:String+delim:String 
            EndIf 

            ' build the key name 
            For char=0 To PeekInt(keybanksize,0)-1 
               If PeekByte(keybank,char)=0 Then Exit 
               key:String=key:String+Chr(PeekByte(keybank,char)) 
            Next 

            Select nType 
               ' read in a String Or binary value 
               Case REG_SZ, REG_BINARY 
                  ' build the value 
                  For char=0 To PeekInt(valbanksize,0)-1 
                     If PeekByte(valbank,char)=0 Then Exit 
                     val:String=val:String+Chr(PeekByte(valbank,char)) 
                  Next 
               ' read in an integer 
               Case REG_DWORD 
                  val:String=PeekInt(valbank,0)                  
            End Select 

            If types 
               cRetVal:String=(cRetVal:String+PeekInt(typebank,0)+"'"+key:String+"="+val:String) 
            Else 
               cRetVal:String=(cRetVal:String+key:String+"="+val:String) 
            EndIf 
         Else 
            Exit 
         EndIf          
          
         nIdx=nIdx+1 
      Forever 
      reg_closekey(hKey) 
   EndIf 
    
   typebank = Null 
   valbank = Null 
   valbanksize = Null 
   keybank = Null 
   keybanksize = Null 
    
   Return cRetVal:String 
End Function 

Rem 
bbdoc: Enumerates the keys contained in the passed subkey And returns them as a delimited String in the format:<br>KEY|KEY|KEY 
EndRem 
Function reg_enumkeys:String(RegKey:Int,SubKey:String,delim:String="|") 
   Local cRetVal:String="" 
   Local keybank:TBank=CreateBank(100) 
   Local nIdx:Int=0,char:Int 

   ' open the key first    
   Local hKey:Int=reg_openkey(RegKey,SubKey:String) 
   If hKey<>-1    
      Repeat          
         If RegEnumKey(hKey,nIdx,BankBuf(keybank),BankSize(keybank))<>REG_ERROR_EOF          
            ' tack on the delimiter 
            If cRetVal:String<>"" 
               cRetVal:String=cRetVal:String+delim:String 
            EndIf 
            
            For char=0 To BankSize(keybank)-1 
               If PeekByte(keybank,char)=0 Then Exit 
               cRetVal:String=cRetVal:String+Chr(PeekByte(keybank,char)) 
            Next 
         Else 
            Exit 
         EndIf 
          
         nIdx=nIdx+1 
      Forever 
      reg_closekey(hKey) 
   EndIf 

   keybank = Null 
   Return cRetVal:String 
End Function 

Function reg_getvalue:String(RegKey:Int,SubKey:String,ValueName:String,Dflt:String="",types:Int=False) 
   Local cRetVal:String=Dflt:String 
   Local hKey:Int=reg_openkey(RegKey,SubKey:String) 
   Local char:Int=0,nType:Int=0 
    
   ' open the key 
   If hKey<>-1 
      Local valbank:TBank=CreateBank(100),valbanksize:TBank=CreateBank(4),typebank:TBank=CreateBank(4) 
    
      ' init the banks 
      PokeInt(typebank,0,0) 
      PokeInt(valbanksize,0,100) 
    
      Local nRslt:Int=RegQueryValueEx(hKey,ValueName:String,0,BankBuf(typebank),BankBuf(valbank),BankBuf(valbanksize)) 
      If (nRslt=REG_ERROR_SUCCESS) 
         cRetVal:String="" 
      
         nType=PeekInt(typebank,0) 
          
         ' build the value 
         Select nType 
            ' read in a String Or binary value 
            Case REG_SZ, REG_BINARY 
               ' build the value 
               For char=0 To PeekInt(valbanksize,0)-1 
                  If PeekByte(valbank,char)=0 Then Exit 
                  cRetVal:String=cRetVal:String+Chr(PeekByte(valbank,char)) 
               Next 
            ' read in an integer 
            Case REG_DWORD 
               cRetVal:String=PeekInt(valbank,0)                  
         End Select 
          
         ' tack on the Type If requested 
         If types 
            cRetVal:String=nType+"'"+cRetVal:String 
         EndIf 
      Else 
         reg_lasterr=nRslt 
      EndIf 
      reg_closekey(hKey) 
   EndIf 
   Return cRetVal:String 
End Function 

Rem 
bbdoc: Returns the registry handle Or -1 If failed 
EndRem 
Function reg_openkey:Int(RegKey:Int,KeyName:String) 
   reg_lasterr=REG_ERROR_SUCCESS 
   Local regbank:TBank=CreateBank(4) 
   Local hKey:Int=-1 

   Local nRslt:Int=RegOpenKey(RegKey:Int,KeyName:String,BankBuf(regbank)) 

   If (nRslt=REG_ERROR_SUCCESS) 
      hKey=PeekInt(regbank,0) 
   Else 
      reg_lasterr=nRslt 
   EndIf 

   regbank = Null 
   Return hKey 
End Function 

Rem 
bbdoc: Closes the registry key. 
returns: #True / #False 
EndRem 
Function reg_closekey:Int(RegKey:Int) 
   reg_lasterr=REG_ERROR_SUCCESS 
   Local nRslt:Int=RegCloseKey(RegKey) 
    
   If nRslt<>REG_ERROR_SUCCESS 
      reg_lasterr=nRslt 
   EndIf 
    
   Return (nRslt=REG_ERROR_SUCCESS) 
End Function 

Rem 
bbdoc: Returns True If the key exists. 
EndRem 
Function reg_iskey:Int(RegKey:Int,KeyName:String) 
   Local hKey:Int=reg_openkey(RegKey,KeyName:String) 
   Local lRetVal:Int=False 

   If hKey<>-1 
      reg_closekey(hKey) 
      lRetVal=True 
   EndIf 
    
   Return lRetVal 
End Function
