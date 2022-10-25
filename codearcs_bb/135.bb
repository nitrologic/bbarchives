; ID: 135
; Author: Vorderman
; Date: 2001-11-14 09:51:24
; Title: Car Physics
; Description: Car physics allowing front/rear/4 wheel drive and under/oversteer

1. FULL 2D SOURCE

Graphics 800,600
SetBuffer BackBuffer()

;physics variables
Global delta_t# = 0.01
Global M_PI# = 3.1415926
Global DRAG# = 5.0
Global RESISTANCE# = 30.0
Global CA_R# = -5.20
Global CA_F# = -5.0
Global MAX_GRIP# = 2.0    
Global MULT# = 57
Global MULT2# = 0.01745

Global CAR1_cartype = 1
Global CAR1_cartype_b# = 1.0
Global CAR1_cartype_c# = 1.0
Global CAR1_cartype_wheelbase# = CAR1_cartype_b# + CAR1_cartype_c#
Global CAR1_cartype_h# = 1.0
Global CAR1_cartype_mass# = 1500
Global CAR1_cartype_inertia# = 1500
Global CAR1_cartype_width# = 1.5
Global CAR1_cartype_length# = 3.0
Global CAR1_cartype_wheellength# = 0.7
Global CAR1_cartype_wheelwidth# = 0.3
Global CAR1_car_position_wc_x# = 400.0
Global CAR1_car_position_wc_y# = 300.0
Global CAR1_car_velocity_wc_x# = 0
Global CAR1_car_velocity_wc_y# = 0
Global CAR1_car_angle# = 0
Global CAR1_car_angularvelocity# = 0
Global CAR1_car_steerangle# = 0
Global CAR1_car_throttle# = 0
Global CAR1_car_brake# = 0

Global front_slip = 0
Global rear_slip = 0

Global velocity_x#
Global velocity_y#
Global yawspeed#
Global sn#, cs#
Global xpos#, ypos#
Global rot_angle#
Global sideslip#
Global slipanglefront#
Global slipanglerear#
Global flatf_x#, flatf_y#
Global flatr_x#, flatr_y#
Global weight#
Global ftraction_x#, ftraction_y# 
Global resistance_x#, resistance_y#
Global force_x#, force_y#
Global torque#
Global acceleration_x#, acceleration_y#, angular_acceleration# 
Global acceleration_wc_x#, acceleration_wc_y#


;main loop
While ( KeyHit(1)=0 )

    Cls

    FUNC_do_input()
    
    FUNC_do_physics()
        
    Flip
    
Wend

End




;input routine from joystick/pad/wheel
Function FUNC_do_input()

    If ( JoyYDir() = -1 ) CAR1_car_throttle# = 2000.0
    If ( JoyYDir() = 0 ) CAR1_car_throttle# = 0.0
    If ( JoyYDir() = 1 ) 
        CAR1_car_throttle# = 0.0
        CAR1_car_brake# = 100.0
    Else
        CAR1_car_brake# = 0.0
    EndIf

    CAR1_car_steerangle# = 0.0
    If ( JoyXDir() = 1 ) CAR1_car_steerangle# = (-M_PI# / 4.0) * 0.15;* MULT2#
    If ( JoyXDir() = -1 ) CAR1_car_steerangle# = (M_PI# / 4.0) * 0.15; MULT2#

    rear_slip = 0;
    If ( JoyDown(1) ) rear_slip = 1;
    
    ;temp display of vars
    Text 0,0,Str(CAR1_car_position_wc_x#)
    Text 0,12,Str(CAR1_car_position_wc_y#)
    Text 0,24,Str(CAR1_car_steerangle#)

    xpos# = CAR1_car_position_wc_x#
    ypos# = CAR1_car_position_wc_y#

    ;render a small moving car rep.
    renda1# =( -CAR1_car_angle# * MULT# ) - 60
    If (renda1#<0.0) Then renda1#=360.0+renda1#
    renda2# =( -CAR1_car_angle# * MULT# ) + 60
    If (renda2#>359.0) Then renda2#=renda2#-360.0
    renda3# =( -CAR1_car_angle# * MULT# ) - 120
    If (renda3#<0.0) Then renda3#=360.0+renda3#
    renda4# =( -CAR1_car_angle# * MULT# ) + 120
    If (renda4#>359.0) Then renda4#=renda4#-360.0

    rendx1#=25*Cos(renda1#) : rendy1#=25*Sin(renda1#)
    rendx2#=25*Cos(renda2#) : rendy2#=25*Sin(renda2#)
    rendx3#=25*Cos(renda3#) : rendy3#=25*Sin(renda3#)
    rendx4#=25*Cos(renda4#) : rendy4#=25*Sin(renda4#)

    Color 255,255,255
    Line rendx1#+xpos#,rendy1#+ypos#,rendx2#+xpos#,rendy2#+ypos#
    Line rendx2#+xpos#,rendy2#+ypos#,rendx4#+xpos#,rendy4#+ypos#
    Line rendx4#+xpos#,rendy4#+ypos#,rendx3#+xpos#,rendy3#+ypos#
    Line rendx3#+xpos#,rendy3#+ypos#,rendx1#+xpos#,rendy1#+ypos#

End Function






Function FUNC_do_physics()

    sn# = Sin(CAR1_car_angle# * MULT#); * MULT2#
    cs# = Cos(CAR1_car_angle# * MULT#) ;* MULT2#

    Text 400,0,"sn="+Str(sn#)+" cs="+Str(cs#)

    ; SAE convention: x is To the front of the car, y is To the Right, z is down
    ; transform velocity in world reference frame To velocity in car reference frame
    velocity_x# = cs# * CAR1_car_velocity_wc_y# + sn# * CAR1_car_velocity_wc_x#
    velocity_y# = -sn# * CAR1_car_velocity_wc_y# + cs# * CAR1_car_velocity_wc_x#

    ; Lateral force on wheels
    ; Resulting velocity of the wheels as result of the yaw rate of the car body
    ; v = yawrate * r where r is distance of wheel To CG (approx. half wheel base)
    ; yawrate (ang.velocity) must be in rad/s
    yawspeed# = CAR1_cartype_wheelbase# * 0.5 * CAR1_car_angularvelocity#

    If( velocity_x# = 0 )
        rot_angle# = 0
    Else 
;        rot_angle# = ATan2( (yawspeed# * MULT#) , (velocity_x# * MULT#) )
        rot_angle# = ATan2( (yawspeed# ) , (velocity_x#) ) * MULT2#
    EndIf

    ; Calculate the side slip angle of the car (a.k.a. beta)
    If( velocity_x# = 0 )
        sideslip# = 0.0
    Else
;        sideslip# = ATan2( (velocity_y# * MULT#) , (velocity_x# * MULT#) )        
        sideslip# = ATan2( (velocity_y# ) , (velocity_x#) )    * MULT2#    
    EndIf

    ; Calculate slip angles For front And rear wheels (a.k.a. alpha)
    slipanglefront# = sideslip# + rot_angle# - CAR1_car_steerangle#
    slipanglerear# = sideslip# - rot_angle#

    ; weight per axle = half car mass times 1G (=9.8m/s^2) 
    weight# = CAR1_cartype_mass# * 9.8 * 0.5
    
    ; lateral force on front wheels = (Ca * slip angle) capped To friction circle * load
    flatf_x# = 0.0
    flatf_y# = CA_F# * slipanglefront#

;    If ( flatf_y# > MAX_GRIP# ) flatf_y# = MAX_GRIP#
;    If ( flatf_y# < -MAX_GRIP# ) flatf_y# = -MAX_GRIP#

    flatf_y# = flatf_y# * weight#
    
    ; allow front wheels to slip
    If(front_slip=1) flatf_y# = flatf_y# * 0.5

    ; lateral force on rear wheels
    flatr_x# = 0.0
    flatr_y# = CA_R# * slipanglerear#

;    If ( flatr_y# > MAX_GRIP# ) flatr_y# = MAX_GRIP#
;    If ( flatr_y# < -MAX_GRIP# ) flatr_y# = -MAX_GRIP#

    flatr_y# = flatr_y# * weight#
    
    If(rear_slip = 1) flatr_y# = flatr_y# * 0.5

    ; longtitudinal force on rear wheels - very simple traction model
    ftraction_x# = 100.0 * (CAR1_car_throttle# - CAR1_car_brake# * Sgn(velocity_x#))
    ftraction_y# = 0.0

    If(rear_slip = 1) ftraction_x# = ftraction_x# * 0.5

    ; Forces And torque on body
    ; drag And rolling resistance
    resistance_x# = -( RESISTANCE# * velocity_x# + DRAG# * velocity_x# * Abs( velocity_x# ) )
    resistance_y# = -( RESISTANCE# * velocity_y# + DRAG# * velocity_y# * Abs( velocity_y# ) )

    ; sum forces
    force_x# = ftraction_x# + (Sin( CAR1_car_steerangle# * MULT# )) * flatf_x# + flatr_x# + resistance_x#
    force_y# = ftraction_y# + (Cos( CAR1_car_steerangle# * MULT# )) * flatf_y# + flatr_y# + resistance_y#    

    ; torque on body from lateral forces
    torque# = CAR1_cartype_b# * flatf_y# - CAR1_cartype_c# * flatr_y#

    ; Acceleration
    ; Newton F = m.a, therefore a = F/m
    acceleration_x# = force_x# / CAR1_cartype_mass#
    acceleration_y# = force_y# / CAR1_cartype_mass#
    
    angular_acceleration# = torque# / CAR1_cartype_inertia#

    ; Velocity And position
    ; transform acceleration from car reference frame To world reference frame
    acceleration_wc_x# = cs# * acceleration_y# + sn# * acceleration_x#
    acceleration_wc_y# = -sn# * acceleration_y# + cs# * acceleration_x#

    ; velocity is integrated acceleration
    CAR1_car_velocity_wc_x# = CAR1_car_velocity_wc_x# + (delta_t# * acceleration_wc_x#)
    CAR1_car_velocity_wc_y# = CAR1_car_velocity_wc_y# + (delta_t# * acceleration_wc_y#)

    ; position is integrated velocity
    CAR1_car_position_wc_x# = CAR1_car_position_wc_x# + (delta_t# * CAR1_car_velocity_wc_x#)
    CAR1_car_position_wc_y# = CAR1_car_position_wc_y# + (delta_t# * CAR1_car_velocity_wc_y#)

    ; Angular velocity And heading
    ; integrate angular acceleration To get angular velocity
    CAR1_car_angularvelocity# = CAR1_car_angularvelocity# + (delta_t# * angular_acceleration#)

    ; integrate angular velocity To get angular orientation
    CAR1_car_angle# = CAR1_car_angle# + (delta_t# * CAR1_car_angularvelocity#)

    If ( CAR1_car_position_wc_x# < 0.0 ) CAR1_car_position_wc_x# = 800.0
    If ( CAR1_car_position_wc_x# > 800.0 ) CAR1_car_position_wc_x# = 0.0
    If ( CAR1_car_position_wc_y# < 0.0 ) CAR1_car_position_wc_y# = 600.0
    If ( CAR1_car_position_wc_y# > 600.0 ) CAR1_car_position_wc_y# = 0.0
    
End Function




2. ADDITIONS / CHANGES for rear/front/4 wheel drive

;type of car - 1=rear 2=front 3=four
Global CAR1_car_drivetype = 3




Function FUNC_do_physics()

    ; SAE convention: x is To the front of the car, y is To the Right, z is down



    ;--------------------------
    ; TRANSFORM VELOCITY
    ;--------------------------
        ; transform velocity in world reference frame To velocity in car reference frame
        sn# = Sin(CAR1_car_angle# * MULT#); * MULT2#
        cs# = Cos(CAR1_car_angle# * MULT#) ;* MULT2#
        velocity_x# = cs# * CAR1_car_velocity_wc_y# + sn# * CAR1_car_velocity_wc_x#
        velocity_y# = -sn# * CAR1_car_velocity_wc_y# + cs# * CAR1_car_velocity_wc_x#


    ;--------------------------
    ; LATERAL FORCES ON WHEELS
    ;--------------------------
        ; Resulting velocity of the wheels as result of the yaw rate of the car body
        ; v = yawrate * r where r is distance of wheel To CG (approx. half wheel base)
        ; yawrate (ang.velocity) must be in rad/s

        ; 0.5 in this line becomes front/rear pos of COG - alter this later
        yawspeed# = CAR1_cartype_wheelbase# * 0.5 * CAR1_car_angularvelocity#
        If( velocity_x# = 0 )
            rot_angle# = 0
        Else 
            rot_angle# = ATan2( (yawspeed# ) , (velocity_x#) ) * MULT2#
        EndIf


    ;-------------------------
    ; SIDESLIP ANGLE
    ;-------------------------
        ; Calculate the side slip angle of the car (a.k.a. beta)
        If( velocity_x# = 0 )
            sideslip# = 0.0
        Else
            sideslip# = ATan2( (velocity_y# ) , (velocity_x#) )    * MULT2#    
        EndIf


    ;-------------------------
    ; SLIP ANGLES FRONT/REAR
    ;-------------------------
        ; Calculate slip angles For front And rear wheels (a.k.a. alpha)
        slipanglefront# = sideslip# + rot_angle# - CAR1_car_steerangle#
        slipanglerear# = (sideslip# - rot_angle#) 


    ;-------------------------
    ; WEIGHT PER AXLE
    ;-------------------------
        ; weight per axle = half car mass times 1G (=9.8m/s^2) 
        ; need to split this into front and rear masses - according to position of COG
        weight# = CAR1_cartype_mass# * 9.8 * 0.5


    ;--------------------------
    ; LATERAL FORCES ON WHEELS 
    ;--------------------------
        ; (Ca * slip angle) capped To friction circle * load
        flatf_x# = 0.0
        flatr_x# = 0.0

        ;check for front / rear / 4 wheel drive
        If ( CAR1_car_drivetype = 1 )

            ;rear wheel drive
            flatr_y# = CA_R# * slipanglerear# / ((CAR1_car_throttle# / 5000.0)+1)
            flatf_y# = CA_F# * slipanglefront#

        Else If ( CAR1_car_drivetype = 2 )

            ;front wheel drive
            flatr_y# = CA_R# * slipanglerear#
            flatf_y# = CA_F# * slipanglefront# / ((CAR1_car_throttle# / 5000.0)+1)

        Else If ( CAR1_car_drivetype = 3 )
    
            ;4 wheel drive
            flatr_y# = CA_R# * slipanglerear# / ((CAR1_car_throttle# / 5000.0)+1)
            flatf_y# = CA_F# * slipanglefront# / ((CAR1_car_throttle# / 5000.0)+1)
        
        EndIf
            
        flatf_y# = flatf_y# * weight#
        flatr_y# = flatr_y# * weight#

        ; allow for handbrake / reduced grip levels - use this for different surfaces later    
        If(front_slip=1) flatf_y# = flatf_y# * 0.5
        If(rear_slip = 1) flatr_y# = flatr_y# * 0.5


    ;-----------------------------
    ; LONGITUDINAL FORCES
    ;-----------------------------
        ; longtitudinal force on rear wheels - very simple traction model
        ftraction_x# = 100.0 * (CAR1_car_throttle# - CAR1_car_brake# * Sgn(velocity_x#))
        ftraction_y# = 0.0

        ; allow for handbrake
        If(rear_slip = 1) ftraction_x# = ftraction_x# * 0.5



    ;-------------------------------
    ; SUM FORCES AND TORQUE ON BODY
    ;-------------------------------
        ; drag And rolling resistance
        resistance_x# = -( RESISTANCE# * velocity_x# + DRAG# * velocity_x# * Abs( velocity_x# ) )
        resistance_y# = -( RESISTANCE# * velocity_y# + DRAG# * velocity_y# * Abs( velocity_y# ) )

        ; sum forces
        force_x# = ftraction_x# + (Sin( CAR1_car_steerangle# * MULT# )) * flatf_x# + flatr_x# + resistance_x#
        force_y# = ftraction_y# + (Cos( CAR1_car_steerangle# * MULT# )) * flatf_y# + flatr_y# + resistance_y#    

        
        ; torque on body from lateral forces
        torque# = CAR1_cartype_b# * flatf_y# - CAR1_cartype_c# * flatr_y#

        ; Acceleration - Newton F = m.a, therefore a = F/m
        acceleration_x# = force_x# / CAR1_cartype_mass#
        acceleration_y# = force_y# / CAR1_cartype_mass#
    
        ; angular acceleration around COG
        angular_acceleration# = torque# / CAR1_cartype_inertia#

        ; Velocity And position
        ; transform acceleration from car reference frame To world reference frame
        acceleration_wc_x# = cs# * acceleration_y# + sn# * acceleration_x#
        acceleration_wc_y# = -sn# * acceleration_y# + cs# * acceleration_x#

        ; velocity is integrated acceleration
        CAR1_car_velocity_wc_x# = CAR1_car_velocity_wc_x# + (delta_t# * acceleration_wc_x#)
        CAR1_car_velocity_wc_y# = CAR1_car_velocity_wc_y# + (delta_t# * acceleration_wc_y#)

        ; position is integrated velocity
        CAR1_car_position_wc_x# = CAR1_car_position_wc_x# + (delta_t# * CAR1_car_velocity_wc_x#)
        CAR1_car_position_wc_y# = CAR1_car_position_wc_y# + (delta_t# * CAR1_car_velocity_wc_y#)

        ; Angular velocity And heading
        ; integrate angular acceleration To get angular velocity
        CAR1_car_angularvelocity# = CAR1_car_angularvelocity# + (delta_t# * angular_acceleration#)

        ; move COG left / right according to body's angular rotation
        CAR1_car_COGx# = (CAR1_car_angularvelocity# * 10.0)
        CAR1_car_COGy# = CAR1_car_throttle# / 100.0
        
        ; integrate angular velocity To get angular orientation
        CAR1_car_angle# = CAR1_car_angle# + (delta_t# * CAR1_car_angularvelocity#)





    ;TEMP - SCREEN LIMITS
    If ( CAR1_car_position_wc_x# < 0.0 ) CAR1_car_position_wc_x# = 800.0
    If ( CAR1_car_position_wc_x# > 800.0 ) CAR1_car_position_wc_x# = 0.0
    If ( CAR1_car_position_wc_y# < 0.0 ) CAR1_car_position_wc_y# = 600.0
    If ( CAR1_car_position_wc_y# > 600.0 ) CAR1_car_position_wc_y# = 0.0
    
End Function

