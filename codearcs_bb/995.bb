; ID: 995
; Author: Jedive
; Date: 2004-04-13 05:41:48
; Title: Newton physics
; Description: The Newton Dynamics engine fully implemented!

.lib "Newton.dll"

;World interface
NewtonCreate%(malloc, free)
NewtonDestroy(world)
NewtonUpdate(world, timestep#)
NewtonDestroyAllBodies(world)
NewtonSetWorldSize(world, min_ptr*, max_ptr*)
NewtonSetBodyLeaveWorldEvent(world, callback)
NewtonWorldFreezeBody(world, body)
NewtonWorldUnfreezeBody(world, body)

;GroupID interface
NewtonMaterialGetDefaultGroupID%(world)
NewtonMaterialCreateGroupID%(world)
NewtonMaterialDestroyAllGroupID(world)
NewtonMaterialSetDefaultCollidable(world, id0, id1, state)
NewtonMaterialSetDefaultFriction(world, id0, id1, static_friction#, kinetic_friction#)
NewtonMaterialSetDefaultElasticity(world, id0, id1, elastic_coef#)
NewtonMaterialSetDefaultSoftness(world, id0, id1, softness_coef#)
NewtonMaterialSetCollisionCallback(world, id0, id1, user_data*, begin_callback, process_callback, end_callback)

;Contact behaviour control interface
NewtonMaterialDisableContact(material)
NewtonMaterialGetContactUserData%(material)
NewtonMaterialGetContactNormalSpeed#(material, contact_handle)
NewtonMaterialGetContactTangentSpeed#(material, contact_handle, index)
NewtonMaterialGetContactPositionAndNormal(material, posit_ptr*, normal_ptr*)
NewtonMaterialGetContactTangentDirections(material, dir0*, dir1*)
NewtonMaterialSetContactSoftness(material, softness#)
NewtonMaterialSetContactElasticity(material, restitution#)
NewtonMaterialSetContactFrictionState(material, state, index)
NewtonMaterialSetContactStaticFrictionCoef(material, coef#, index)
NewtonMaterialSetContactKineticFrictionCoef(material, coef#, index)
NewtonMaterialSetContactTangentAcceleration(material, accel#, index)
NewtonMaterialContactRotateTangentDirections(material,align_vector*)

;Convex collision primitives interface
NewtonCreateSphere%(world, radius#, offset_matrix*)
NewtonCreateBox%(world, dx#, dy#, dz#, offset_matrix*)

;Complex collision primitives interface
NewtonCreateCompoundCollision%(world, count, collision_primitive_array*)
NewtonCreateUserMeshCollision%(world, min_box*, max_box*, user_data*, collide_callback, rayhit_callback, destroy_callback)
NewtonCreateTreeCollision%(world, user_callback)
NewtonTreeCollisionBeginBuild(tree_collision)
NewtonTreeCollisionAddFace(tree_collision, vertex_count, vertex_ptr, stride_in_bytes, face_attribute)
NewtonTreeCollisionEndBuild(tree_collision, optimize)
NewtonTreeCollisionSerialize(tree_collision, callback, serialize_handle*)
NewtonCreateTreeCollisionFromSerialization%(world, user_callback, deserialize_function, serialize_handle)
NewtonTreeCollisionGetFaceAtribute%(tree_collision, face_index_array*)
NewtonTreeCollisionSetFaceAtribute(tree_collision, face_index_array*, attribute)

;Collision miscelanios interface
NewtonReleaseCollision(world, collision_ptr)
NewtonCollisionCalculateAABB(collision_ptr, offset_matrix*, p0*, p1*)

;Transforms utility functions
NewtonGetEulerAngle(matrix*, angles*)
NewtonSetEulerAngle(matrix*, angles*)

;Rigid Body interface
NewtonCreateBody%(world, collision_ptr)
NewtonDestroyBody(world, body)
NewtonBodySetUserData(body, user_data*)
NewtonBodyGetUserData%(body)
NewtonBodySetTransformCallback(body, callback)
NewtonBodySetForceAndTorqueCallback(body, callback)
NewtonBodySetDestructorCallback(body, callback)
NewtonBodySetMassMatrix(body, mass#, Ixx#, Iyy#, Izz#)
NewtonBodyGetMassMatrix(body, mass*, Ixx*, Iyy*, Izz*)
NewtonBodyGetInvMass(body, inv_mass*, inv_Ixx*, inv_Iyy*, inv_Izz*)
NewtonBodySetMatrix(body, matrix_ptr*)
NewtonBodySetMatrixRecursive(world, body, matrix_ptr*)
NewtonBodyGetMatrix(body, matrix_ptr*)
NewtonBodySetForce(body, force_ptr*)
NewtonBodyAddForce(body, force_ptr*)
NewtonBodyGetForce(body, vector*)
NewtonBodySetTorque(body, torque_ptr*)
NewtonBodyAddTorque(body, torque_ptr*)
NewtonBodyGetTorque(body, vector*)
NewtonBodyAddBuoyancyForce(body, fluid_density#, fluid_linear_viscosity#, fluid_angular_viscosity#, gravity_vector*, buoyancy_plane)
NewtonBodySetCollision(body, collision)
NewtonBodyGetCollision%(body)
NewtonBodySetMaterialGroupID(body, id)
NewtonBodyGetMaterialGroupID%(body)
NewtonBodySetAutoFreeze(body, state)
NewtonBodyGetAutoFreeze%(body)
NewtonBodySetFreezeTreshold(body, freeze_accel_mag#, freeze_alpha_mag#, freeze_speed_mag#, freeze_omega_mag#)
NewtonBodyGetFreezeTreshold(body, freeze_accel_mag*, freeze_alpha_mag*, freeze_speed_mag*, freeze_omega_mag*)
NewtonBodyGetAABB(body, p0*, p1*)
NewtonBodySetVelocity(body, velocity*)
NewtonBodyGetVelocity(body, velocity*)
NewtonBodySetOmega(body, omega*)
NewtonBodyGetOmega(body, omega*)
NewtonBodySetLinearDamping(body, linear_damp#)
NewtonBodyGetLinearDamping(body, linear_damp*)
NewtonBodySetAngularDamping(body, angular_damp*)
NewtonBodyGetAngularDamping(body, angular_damp*)
NewtonBodyRayIntersect#(body, p0*, p1*, normal_ptr*)
NewtonAddBodyImpulse(world, body, point_delta_veloc*, point_posit*)
