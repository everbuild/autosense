
lightControlModule("1");

var b = button("1:0", "kitchen").onPress(function (e) {
    print('pressed button ' + e.getSource().getName());
});

// simulate a button press just for testing
b.press();

/*
button("1:0", "kitchen").onPress(cycle(lights.kitchen1, lights.kitchen2));
button("1:1", "hall").onPress(toggle(lights.hall));
button("1:2", "all off").onPress(turnOff(lights));
*/

//pressence("1:3", "pressense wc").onStart(turnOn(lights.wc)).onEnd(turnOff(lights.wc));

//lightControlModule(1);
