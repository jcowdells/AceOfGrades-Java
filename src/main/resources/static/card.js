const default_state = "not-flipped";

// Controller - keeps track of things in the document
class Controller {
    constructor() {
        // content is the main area of the page
        this.content = document.getElementById("content");
        this.content.onmousemove = this.mouseMoveCallback.bind(this);
        this.content.onmousedown = this.mousePressCallback.bind(this);
        this.content.onmouseup = this.mouseReleaseCallback.bind(this);
        this.mouse_x = 0;
        this.mouse_y = 0;
        this.closest_zone = null;
    }

    mouseMoveCallback(event) {
        this.mouse_x = event.clientX;
        this.mouse_y = event.clientY;
        page.stack.mouseMoveCallback(event);
    }

    mousePressCallback(event) {
        getState().state.checkState("press");
    }

    mouseReleaseCallback(event) {
        let shortest_dist = Number.POSITIVE_INFINITY;
        let mouse_pos = {
            x: event.clientX,
            y: event.clientY
        }
        let touching_zone = false;
        for (let zone_id in zones) {
            let zone = zones[zone_id];
            let curr_dist = zone.getMidpointSquareDistance(mouse_pos)
            if (curr_dist < shortest_dist) {
                this.closest_zone = zone;
                shortest_dist = curr_dist;
            }
            if (zone.containsCoordinate(mouse_pos)) {
                touching_zone = true;
            }
        }

        if (!touching_zone) {
            retreatState();
            return;
        }

        getState().state.checkState("release");
    }

    getClosestZone() {
        if (this.closest_zone == null) {
            return zones.stack;
        } else {
            return this.closest_zone;
        }
    }

    getMousePosition() {
        return {
            x: this.mouse_x,
            y: this.mouse_y
        }
    }
}

// global controller instance
let controller = new Controller();

// Zone is a place where cards can end up
class Zone {
    constructor(zone_id) {
        this.zone = document.getElementById(zone_id);
        this.zone_id = zone_id;
    }

    getPosition() {
        let rect = this.zone.getBoundingClientRect();
        return {
            x: rect.left,
            y: rect.top
        }
    }

    getMidpointSquareDistance(coordinate) {
        let rect = this.zone.getBoundingClientRect();
        let mid_x = rect.left + rect.width / 2;
        let mid_y = rect.top + rect.height / 2;

        let delta_x = mid_x - coordinate.x;
        let delta_y = mid_y - coordinate.y;

        return delta_x * delta_x + delta_y * delta_y;
    }

    containsCoordinate(coordinate) {
        let rect = this.zone.getBoundingClientRect();
        if (coordinate.x > rect.right) {
            return false;
        } else if (coordinate.y > rect.bottom) {
            return false;
        } else if (coordinate.x < rect.left) {
            return false;
        } else if (coordinate.y < rect.top) {
            return false;
        }
        return true;
    }
}

// State is a way the quiz is acting right now. Determines how the cards behave at any time
class State {
    constructor(time, angle, change_event, reset_position=true) {
        this.time = time;
        this.angle = angle;
        this.change_event = change_event;
        this.s_pos = {
            x: null,
            y: null
        }
        this.reset_position = reset_position;
    }

    checkState(event) {
        if (event === this.change_event) {
            advanceState();
        }
    }

    updateState() {
        this.s_pos = controller.getMousePosition();

        // if the change reason is to just wait x seconds, then set a timeout to change it
        if (this.change_event === "time-elapsed") {
            setTimeout(function() {
                advanceState();
            }, this.time * 1000);
        }
    }

    static applyAngle(element, angle, flip_angle=false) {
        if (flip_angle) {
            angle += Math.PI;
        }
        element.style.transform = "rotateY(" + angle + "rad)";
    }

    static applyTime(element, time) {
        element.style.transition = "all " + time + "s ease-in-out";
    }

    static applyPosition(element, x, y) {
        element.style.left = x + "px";
        element.style.top = y + "px";
    }

    applyState(element, flip_angle=false) {
        State.applyAngle(element, this.angle, flip_angle);
        State.applyTime(element, this.time);
        if (this.reset_position) {
            State.applyPosition(element, 0, 0);
        }
    }

    getPosition() {
        return this.s_pos;
    }
}

// Active Stack is the stack where new cards appear
class ActiveStack {
    constructor(container_id, front_id, back_id, next_id) {
        this.container = document.getElementById(container_id);
        this.front = document.getElementById(front_id);
        this.back = document.getElementById(back_id);
        this.next = document.getElementById(next_id);
        this.state = default_state;
    }

    updateFlipAngle() {
        let s_pos = getState(this.state).state.getPosition();
        let mouse_pos = controller.getMousePosition();

        let rect = this.container.getBoundingClientRect();
        let radius = rect.width / 2;
        let mid_x = rect.left + radius;

        let angle = Math.atan2(radius, mouse_pos.x - mid_x);
        if (mid_x > s_pos.x) {
            angle = Math.PI - angle;
        } else {
            angle = Math.PI * 2 - angle;
        }

        State.applyAngle(this.front, angle);
        State.applyAngle(this.back, angle, true);
    }

    updatePosition() {
        let s_pos = state_graph[this.state].state.getPosition();
        let position = controller.getMousePosition();

        let x = position.x - s_pos.x;
        let y = position.y - s_pos.y;

        State.applyPosition(this.front, x, y);
        State.applyPosition(this.back, x, y);
    }

    updatePositionTarget() {
        let rect = this.container.getBoundingClientRect();
        let target = controller.getClosestZone().getPosition();

        let x = target.x - rect.left;
        let y = target.y - rect.top;

        State.applyPosition(this.front, x, y);
        State.applyPosition(this.back, x, y);
    }

    mouseMoveCallback(event) {
        if (this.state === "flipping") {
            this.updateFlipAngle();
        }

        if (this.state === "dragging") {
            this.updatePosition();
        }
    }

    applyState(new_state_name, new_state) {
        this.state = new_state_name;
        new_state.applyState(this.front);
        new_state.applyState(this.back, true);

        // #bodgemethod
        State.applyTime(this.next, new_state.time);

        if (this.state === "flipping") {
            this.updateFlipAngle();
        }

        if (this.state === "snapping") {
            this.updatePositionTarget();
        }
    }
}

// Depleted stack is where tested cards end up
class DepletedStack {
    constructor(stack_id) {
        this.stack = document.getElementById(stack_id);
        this.state = default_state;
    }

    applyState(new_state_name, new_state) {
        this.state = new_state_name;
        State.applyTime(this.stack, new_state.time);
    }
}

// page is all the main elements on the page
let page = {
    stack: new ActiveStack("card-container", "card-front", "card-back", "card-next"),
    correct: new DepletedStack("stack-correct"),
    incorrect: new DepletedStack("stack-incorrect")
}

// zones are places the cards can end up
let zones = {
    stack: new Zone("card-container"),
    correct: new Zone("stack-correct"),
    incorrect: new Zone("stack-incorrect")
}

// adjacency list for state traversal
let state = default_state;
let state_graph = {
    "not-flipped": {
        state: new State(0.0, 0.0, "press"),
        next: "flipping",
        prev: "snapping"
    },
    "flipping": {
        state: new State(0.0, 0.0, "release"),
        next: "flipped",
        prev: "not-flipped"
    },
    "flipped": {
        state: new State(0.5, Math.PI, "press"),
        next: "dragging",
        prev: "flipping"
    },
    "dragging": {
        state: new State(0.0, Math.PI, "release"),
        next: "snapping",
        prev: "flipped"
    },
    "snapping": {
        state: new State(0.5, Math.PI, "time-elapsed"),
        next: "not-flipped",
        prev: "dragging"
    },
}

function getState() {
    return state_graph[state];
}

function advanceState() {
    let curr_state = state_graph[state];
    state = curr_state.next;
    state_graph[state].state.updateState();
    updatePage();
}

function retreatState() {
    let curr_state = state_graph[state];
    state = curr_state.prev;
    state_graph[state].state.updateState();
    updatePage();
}

function updatePage() {
    for (let element_id in page) {
        let element = page[element_id];
        element.applyState(state, getState().state);
    }
}