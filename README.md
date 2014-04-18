# nesemu
This is a simple, no-frills NES emulator that I created for learning purposes
in two weeks.
It was during a low-load period at school and because I was bored.

It's buggy and missing a lot of functionality, and I don't actually recommend
using this. Have a look at FCEUX and Nestopia, which are vastly superior.

Because writing emulators in Java is _insane_, I naturally gave it a shot.
The code is fairly modular and clean in comparison to some other emulators
out there. Performance shortcuts were sacrificed to obtain cleaner code, but
for Java it's still pretty fast. Lots of indirection was used with the CPU
instructions in particular.

Am I to do this again, it would be in C or another language with deterministic
execution like rust.

## Packages
### gui
The front-end driver that uses Swing to display graphics and get input.
### machine6502
The 6502 CPU machine logic, and _only_ the CPU logic.
### memory
Memory mapping implementations for use with the 6502 machine.
Technically separate from machine6502, used by the NES engine.
### nes
The NES engine. Handles the loading and execution of ROMs.
The `nes.GameRunnable` class, when constructed and run, will notify a callback
when a buffer is ready to be displayed. In this cases, the GUI is notified.

