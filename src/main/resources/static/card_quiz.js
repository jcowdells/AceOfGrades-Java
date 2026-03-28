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
    // extract cards data
    const cards = cards_data["cards"]
    const quiz_style = cards_data["quiz-style"]
    const post_results = cards_data["post-results"];

    // card completion data
    let quiz_data = [];

    // card elements
    const card_container = document.getElementById("card-container");
    const card_front = document.getElementById("card-front");
    const card_back = document.getElementById("card-back");
    const card_next = document.getElementById("card-next");

    // stack elements
    const stack_correct = document.getElementById("stack-correct");
    const stack_incorrect = document.getElementById("stack-incorrect");

    function transition(time) {
        card_front.style.transition = "all " + time + "s ease-in-out";
        card_back.style.transition = "all " + time + "s ease-in-out";
        card_next.style.transition = "all " + time + "s ease-in-out";
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

    function resetSize() {
        card_front.style.width = "100%";
        card_front.style.height = "100%";
        card_back.style.width = "100%";
        card_back.style.height = "100%";
    }

    function resizeNext(percent) {
        card_next.style.width = percent + "%";
        card_next.style.height = percent + "%";
        card_next.style.fontSize = (percent / 100) + "rem";
    }

    function setFontSize(font_size) {
        card_front.style.fontSize = font_size + "rem";
        card_back.style.fontSize = font_size + "rem";
    }

    function finishPack() {
        if (post_results) {
            const pack_id = document.getElementById("game-container").getAttribute("data-pack-id");
            console.log(quiz_data);
            fetch(
                `/api/packs/${pack_id}/quiz/complete`, {
                    method: "POST",
                    headers: {
                        "Accept": "application/json",
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({cards: quiz_data})
                }
            ).then(
                response => {
                    if (response.ok) {
                        return response.json();
                    }
                    return Promise.reject(response);
                }
            ).catch(error => {
                error.text().then(
                    text => {
                        const content = document.getElementById("content");
                        content.innerHTML = text;
                    }
                );
            });
        }
        document.getElementById("game-container").innerHTML = document.getElementById("game-complete-template").innerHTML;
    }

    function switchToNextCard(first_card=false) {
        if (!first_card) cards.shift();
        if (cards.length === 0) {
            card_front.remove();
            card_back.remove();
            finishPack();
            return;
        }
        card_front.innerHTML = cards[0]["front"];
        card_back.innerHTML = cards[0]["back"];
        card_front.style.background = cards[0]["front_color"];
        card_back.style.background = cards[0]["back_color"];
        if (cards.length > 1) {
            card_next.innerHTML = cards[1]["front"];
            card_next.style.background = cards[1]["front_color"];
        } else {
            card_next.remove();
        }
    }

    function moveToStack(stack, correct) {
        if (cards.length === 0)
            return;

        const card_id = cards[0]["id"];
        const quiz_index = quiz_data.findIndex(q => q["id"] === card_id);
        if (quiz_index >= 0) {
            quiz_data[quiz_index]["attempts"] += 1;
            if (correct) quiz_data[quiz_index]["correct"] += 1;
        } else {
            quiz_data.push({
                "id": card_id,
                "attempts": 1,
                "correct": correct ? 1 : 0
            })
        }

        if (!correct && quiz_style === "burnout") {
            // add card back to the pile in burnout mode
            cards.push(cards[0]);
        }

        // update variables
        transitioning = true;
        let stack_rect = stack.getBoundingClientRect();
        let card_rect = card_container.getBoundingClientRect();
        let font_size = stack_rect.height / card_rect.height;

        // set card transition in motion
        transition(1);
        move(stack_rect.left - card_rect.left, stack_rect.top - card_rect.top);
        resize(stack_rect.width, stack_rect.height);
        resizeNext(100);
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
            resetSize();
            setFontSize(1);

            // set stack styles
            stack.replaceChildren(...card_back.cloneNode(true).childNodes);
            stack.style.fontSize = font_size + "rem";
            stack.style.background = card_back.style.background;

            resizeNext(80);

            switchToNextCard();
            }, 1000);
    }

    stack_correct.addEventListener("click", function(event) {
        if (switched) {
            moveToStack(stack_correct, true);
        }
    });

    stack_incorrect.addEventListener("click", function(event) {
        if (switched) {
            moveToStack(stack_incorrect, false);
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
                moveToStack(stack_correct, true);
            } else {
                moveToStack(stack_incorrect, false);
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

    switchToNextCard(true);
}

function onLoadEditor() {

}

document.body.addEventListener("htmx:load", function(event) {
    const game_container = document.getElementById("game-container");
    const pack_id = game_container.getAttribute("data-pack-id");
    const quiz_style = game_container.getAttribute("data-quiz-style");
    const num_cards = Number(game_container.getAttribute("data-num-cards"));
    if (editing) {
        onLoadEditor();
    } else {
        fetch(
            `/api/packs/${pack_id}/cards/`, {
                method: "POST",
                "headers": {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    "quiz-style": quiz_style,
                    "num-cards": num_cards
                })
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
            console.log(error);
            error.text().then(
                text => {
                    const content = document.getElementById("content");
                    content.innerHTML = text;
                }
            );
        });
    }
});