# Catcheroid

Created by Willian Hua and Shotaro Takada

Catcheroid

What is it?
● Android implementation of classic claw
catcher arcade game
● Uses gyroscope+accelerometer to move claw
● Two game modes!
o Classic and Time Trial

Features
● Use gyroscope and accelerometer to control
claw
● Catch prizes below
● Catchy music
● Move too abruptly and prize will fall
● Multiple Levels (3 for now) for classic mode
● Time trial mode
● Avoid Obstacles going up

Bugs
● Claw composed of one sprite instead of two
independent “claws” clamping in on prize
● Never actually used physics component of
gravity to move claw down
● Prize sometimes goes off screen due to
knocking down

Frameworks Used
● AndEngine
o AndEnginePhysicsBox2DExtension
o AndEngineDebugDrawExtension 
