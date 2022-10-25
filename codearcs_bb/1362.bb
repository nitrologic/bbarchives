; ID: 1362
; Author: jfk EO-11110
; Date: 2005-04-29 12:48:14
; Title: Coordinated Flip
; Description: No more invisible Windows Popups in Fullscreen

;Sometimes Windows Api Messages such as System Alerts or 
;Firewall Queries remain invisible because they are drawn to 
;the backbuffer and pause the Blitz App in Fullscreen Mode.

;This is about how to make sure Windows Messages won't be 
;disguised in the backbuffer in fullscreen mode.

;Why are the messages not drawn everytime to the front  
;buffer or to the backbuffer, why is it some kind of random 
;game with them?

;The reason why is there are two buffers, lets name them 
;Buffer 1 and Buffer 2. They always remain Buffer 1 and 2, 
;no matter if we made them Frontbuffer or Backbuffer! And 
;windows (as usual) doesn't know about frontbuffer or 
;backbuffer and simply writes to Buffer 1 all the time.

;So all we have to do is watch every flip closely and use a 
;counter that will tell us if we are watching Buffer 1 or 2.

;This is pretty easy, simply replace your current Flip 
;commands with a function call. EG:

global flipcounter
...

CoFlip() ; replaces a normal Flip

...

function CoFlip(waitsync=1)
 flip waitsync
 flipcounter=flipcounter xor 1
end function

;now right before we call a function or external Exe that will
;force some Win Api High priority Alert (like when you 
;connect to the internet and eg. a Firewall like Zonealarm 
;throws a prompt to the user), to make sure your game won't 
;freeze with an alert in the backbuffer that waits for user 
;action, all you have to do is this:

if flipcounter=1 then CoFlip(0)

;of course, you may also add an additional Renderworld 
;before to make sure everything looks nice.
