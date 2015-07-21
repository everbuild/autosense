
function lightControlModule(address) {
    return context.createLightControlModule(address);
}

function button(binding, name) {
    var button = context.createButton(name);
    context.bindButton(button, binding);
    return button;
}

function forEachArg(args, fn) {
    if (args.length == 1) {
        var arg = args[0];
        if (Array.isArray(arg)) {
            return function () {
                arg.forEach(function (t) {
                    fn.apply(t, arguments);
                });
            };
        } else {
            return function () {
                fn.apply(arg, arguments);
            };
        }
    } else if(args.length > 1) {
        return function () {
            for(var i = 0; i < args.length; i ++) {
                fn.apply(args[i], arguments);
            }
        };
    } else {
        return function () {};
    }
}

function toggle() {
    return forEachArg(arguments, function (ev) {
        this.toggle();
    });
}

function turnOn(light) {
    return forEachArg(arguments, function (ev) {
        this.turnOn();
    });
}

function turnOff(light) {
    return forEachArg(arguments, function (ev) {
        this.turnOff();
    });
}

function cycle() {
    // we'll create a circular linked list of lights though which to cycle
    // this dummy entry serves as the "off" entry
    var last = {
        turnOn: function () {},
        turnOff: function () {}
    };
    var current = last;
    var first = last;
    for(var i = arguments.length - 1; i >= 0; i --) {
        var light = arguments[i];
        first = {
            turnOn: turnOn(light),
            turnOff: turnOff(light),
            next: first
        };
        if(light.isOn()) {
            current = first;
        }
    }
    last.next = first;

    return function (ev) {
        current.turnOff();
        current = current.next;
        current.turnOn();
    }
}
