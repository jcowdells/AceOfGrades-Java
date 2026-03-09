const MOUSE_DISTANCE = 100;

let editing = false;
let clicked = false;
let flipped = false;
let switched = false;
let transitioning = false;
let side = "neither";
let spin_fraction = 0;
let click_pos = {"x": 0, "y": 0};

function rectContainsPoint(rect, x, y) {
    if (x < rect.left) return false;
    if (x > rect.right) return false;
    if (y < rect.top) return false;
    if (y > rect.bottom) return false;
    return true;
}

function onLoadQuiz(cards_data) {
    console.log(cards_data);

    // card elements
    const card_container = document.getElementById("card-container");
    const card_front = document.getElementById("card-front");
    const card_back = document.getElementById("card-back");

    // stack elements
    const stack_correct = document.getElementById("stack-correct");
    const stack_incorrect = document.getElementById("stack-incorrect");

    function transition(time) {
        card_front.style.transition = "all " + time + "s ease-in-out";
        card_back.style.transition = "all " + time + "s ease-in-out";
    }

    function rotate() {
        angle = spin_fraction * Math.PI;
        card_front.style.transform = "rotateY(" + angle + "rad)";
        card_back.style.transform = "rotateY(" + (angle + Math.PI) + "rad)";
    }

    function move(x, y) {
        card_front.style.left = x + "px";
        card_back.style.top = y + "px";
        card_back.style.left = x + "px";
        card_back.style.top = y + "px";
    }

    function resize(width, height) {
        card_front.style.width = width + "px";
        card_back.style.height = height + "px";
        card_back.style.width = width + "px";
        card_back.style.height = height + "px";
    }

    function setFontSize(font_size) {
        card_front.style.fontSize = font_size + "rem";
        card_back.style.fontSize = font_size + "rem";
    }

    function moveToStack(stack) {
        // update variables
        transitioning = true;
        let stack_rect = stack.getBoundingClientRect();
        let card_rect = card_container.getBoundingClientRect();
        let font_size = stack_rect.height / card_rect.height;

        // set card transition in motion
        transition(1);
        move(stack_rect.left - card_rect.left, stack_rect.top - card_rect.top);
        resize(stack_rect.width, stack_rect.height);
        setFontSize(font_size);

        // set stack styles
        stack.style.border = "none";

        // after transition is complete, reset the state
        setTimeout(function() {
            // update variables
            flipped = false;
            switched = false;
            transitioning = false;
            spin_fraction = 0;
            stack_rect = stack.getBoundingClientRect();
            card_rect = card_container.getBoundingClientRect();

            // reset card styles
            transition(0);
            rotate();
            move(0, 0);
            resize(card_rect.width, card_rect.height);
            setFontSize(1);

            // set stack styles
            stack.replaceChildren(...card_back.cloneNode(true).childNodes);
            stack.style.fontSize = font_size + "rem";
            stack.style.background = card_back.style.background;
            }, 1000);
    }

    stack_correct.addEventListener("click", function(event) {
        if (switched) {
            moveToStack(stack_correct);
        }
    });

    stack_incorrect.addEventListener("click", function(event) {
        if (switched) {
            moveToStack(stack_incorrect);
        }
    });

    card_container.addEventListener("mousedown", function(event) {
        if (transitioning) {
            return;
        }

        // update clicked status
        clicked = true;
        click_pos.x = event.clientX;
        click_pos.y = event.clientY;

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

        // mark as not clicked.
        clicked = false;
        side = "neither";
        const was_flipped = flipped;
        flipped = false;

        // make transitions be 1 seconds
        transition(1);

        const rect = card_container.getBoundingClientRect();

        // if the mouse is more than
        if (switched && event.clientX - click_pos.x > (rect.right - rect.left) * 0.5) {
            if (event.clientY - rect.top - (rect.bottom - rect.top) * 0.5 < 0) {
                moveToStack(stack_correct);
            } else {
                moveToStack(stack_incorrect);
            }
            return;
        }

        if (was_flipped || rectContainsPoint(rect, event.clientX, event.clientY)) {
            switched = !switched;
        }

        move(0, 0);

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
    }

    document.onmousemove = function(event) {
        // if mouse not down, then ignore
        if (!clicked) {
            return;
        }

        // get midpoint
        const rect = card_container.getBoundingClientRect();
        const midpoint = (rect.left + rect.right) / 2;

        if (switched) {
            const x = event.clientX - click_pos.x;
            const y = event.clientY - click_pos.y;
            move(x, y);
            return;
        }

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
}

function onLoadEditor() {

}

document.body.addEventListener("htmx:load", function(event) {
    const params_string = window.location.search;
    const params = new URLSearchParams(params_string);
    const pack = params.get("pack")
    if (editing) {
        onLoadEditor();
    } else {
        fetch(
            `/api/packs/1/cards/`, {
                method: "POST"
            }
        ).then(
            response => {
                if (response.ok) {
                    return response.json();
                }
                return Promise.reject(response);
            }
        ).then(
            json => onLoadQuiz(json)
        ).catch(error => {
            error.text().then(
                text => {
                    const content = document.getElementById("content");
                    content.innerHTML = text;
                }
            );
        });
    }
});