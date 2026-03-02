const MOUSE_DISTANCE = 100;

let editing = false;
let clicked = false;
let flipped = false;
let switched = false;
let side = "neither";
let spin_fraction = 0;

function rectContainsPoint(rect, x, y) {
    if (x < rect.left) return false;
    if (x > rect.right) return false;
    if (y < rect.top) return false;
    if (y > rect.bottom) return false;
    return true;
}

function onLoadQuiz() {
    // card elements
    const card_container = document.getElementById("card-container");
    const card_front = document.getElementById("card-front");
    const card_back = document.getElementById("card-back");

    // stack elements
    const stack_correct = document.getElementById("stack-correct");
    const stack_incorrect = document.getElementById("stack_incorrect");

    function transition(time) {
        card_front.style.transition = "all " + time + "s ease-in-out";
        card_back.style.transition = "all " + time + "s ease-in-out";
    }

    card_container.addEventListener("mousedown", function(event) {
        // update clicked status
        clicked = true;

        // decide which side of the card has been clicked (determines rotation direction)
        const rect = card_container.getBoundingClientRect();
        const midpoint = (rect.left + rect.right) / 2;
        if (event.clientX > midpoint) {
            side = "right";
        } else {
            side = "left";
        }

        // make transitions be 0 seconds
        transition(0);
    });

    function rotate() {
        angle = spin_fraction * Math.PI;
        card_front.style.transform = "rotateY(" + angle + "rad)";
        card_back.style.transform = "rotateY(" + (angle + Math.PI) + "rad)";
    }

    function repairSpin() {
        // if clicked, don't attempt to change anything.
        if (clicked) {
            return;
        }

        // make transitions be 0 seconds
        transition(0);

        spin_fraction %= 2;
        rotate();
    }

    document.onmouseup = function(event) {
        if (side === "neither") {
            return;
        }
        // make transitions be 1 seconds
        transition(1);

        if (flipped || rectContainsPoint(card_container.getBoundingClientRect(), event.clientX, event.clientY)) {
            switched = !switched;
        }

        if (switched) {
            if (spin_fraction > 0) {
                spin_fraction = 1;
            } else {
                spin_fraction = -1;
            }
        } else {
            // spin around to the closest full spin.
            if (spin_fraction > 1) {
                spin_fraction = 2;
            } else if (spin_fraction < -1) {
                spin_fraction = -2;
            } else {
                spin_fraction = 0;
            }
            // after 1000ms, repair the spin
            setTimeout(repairSpin, 1000);
        }

        rotate();

        // mark as not clicked.
        clicked = false;
        side = "neither";
    }

    document.onmousemove = function(event) {
        // if mouse not down, then ignore
        if (!clicked) {
            return;
        }

        // get midpoint
        const rect = card_container.getBoundingClientRect();
        const midpoint = (rect.left + rect.right) / 2;

        const angle = Math.atan2(MOUSE_DISTANCE, midpoint - event.clientX);
        spin_fraction = angle / Math.PI;
        if (switched) {
            spin_fraction += 1;
        }
        if (side === "right") {
            spin_fraction -= 1;
        }
        rotate();

        if (event.clientX > midpoint && side === "left") {
            flipped = true;
        } else if (event.clientX < midpoint && side === "right") {
            flipped = true;
        } else {
            flipped = false;
        }
    }

    stack_correct.addEventListener("mouseup", function(event) {
        if (flipped) {

        }
    });

    stack_incorrect.addEventListener("mousedown", function(event) {
        if (flipped) {

        }
    });
}

function onLoadEditor() {

}

document.body.addEventListener("htmx:load", function(event) {
    if (editing) {
        onLoadEditor();
    } else {
        onLoadQuiz();
    }
});