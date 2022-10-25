; ID: 1150
; Author: AntMan - Banned in the line of duty.
; Date: 2004-08-30 05:17:35
; Title: Leaf Texture packer
; Description: Pack multiple textures into one bigger one with funcs to convert u,v coords.

Type lMap
   Field tree.leaf,tex
   Field w,h,texbuf
End Type
Type leaf
     Field leaf.leaf[2]
     Field x#,y#,w#,h#
     Field on,own.lmap
End Type

Function newTexture.lmap(width,height,flag=0) ;creates a new big/container texture.  
    out.lmap=New lmap
    out\tex=CreateTexture(width,height,flag)
    out\w=width
    out\h=height
   out\texBuf=TextureBuffer(out\tex)
   Return out
End Function 


;this packs <texture> into the lmap returned by the above func. It then returns a 'leaf' object you use to access it.
Function newLeaf.leaf(lmap.lmap,texture)
  width =TextureWidth(texture)
  height =TextureHeight(texture)

   If width<1 Or height<1 Return
   If lMap\tree =Null ;first image
      lMap\tree =New leaf
      lMap\tree\w =lmap\w
      lMap\tree\h =lmap\h
      lmap\tree\own=lmap
  EndIf
  For leaf.leaf =Each leaf
       If leaf\own=lmap
         out.leaf =insertLeaf( leaf,texture)
         If out<>Null 
            out\own=lmap 
            Return out
         EndIf
       EndIf
   Next
End Function


Function insertLeaf.leaf( leaf.leaf,texture)
  width =TextureWidth(texture)
  height =TextureHeight(texture)
  If leaf\on Return 
  If width<=leaf\w And height<=leaf\h ;fits
  leaf\on =True

  leaf\leaf[0] =New leaf
  leaf\leaf[1] =New leaf
  leaf\leaf[0]\x =leaf\x+width
  leaf\leaf[0]\y =leaf\y
  leaf\leaf[0]\w =leaf\w-width-1
  leaf\leaf[0]\h =height

  leaf\leaf[1]\x =leaf\x
  leaf\leaf[1]\y =leaf\y+height
  leaf\leaf[1]\w =leaf\w
  leaf\leaf[1]\h =leaf\h-height
  leaf\w =width
  leaf\h =height
  CopyRect 0,0,width,height,leaf\x,leaf\y,TextureBuffer(texture),leaf\own\texbuf
  Return leaf
EndIf
End Function


;use this on the leaf returned by the above, it converts
;a valid 0 To 1 u coord into the actual u,v coord it is
;on the bigger lmap. (
Function leafU#(leaf.leaf,u#) ;converts a normal u coord into a lmap u coord
Return ((leaf\x+1)+((leaf\w-2)*u))/leaf\own\w 
End Function

Function leafV#(leaf.leaf,v#)
        Return ((leaf\y+1)+((leaf\h-2)*v))/leaf\own\h
End Function
