Please note for running this program it must be done like so:

./vmsim -n <numframes> -a <algorithm> [-r <refresh num>] file.trace

The arguments must be done in this order. For any algorithm other than nru
it cannot have the -r flag. For example

./vmsim -n 64 -a opt swim.trace
and
./vmsim -n 32 -a nru -r 400 gcc.trace

are both acceptable ways to run this program.
