const MOUSE_DISTANCE = 100;

let editing = false;
let clicked = false;
let flipped = false;
let switched = false;
let transitioning = false;
let side = "neither";
let spin_fraction = 0;
let click_pos = {"x": 0, "y": 0};
let cards = [];

function rectContainsPoint(rect, x, y) {
    if (x < rect.left) return false;
    if (x > rect.right) return false;
    if (y < rect.top) return false;
    if (y > rect.bottom) return false;
    return true;
}

function onLoadQuiz(cards_data) {
    // extract cards data
    cards = cards_data["cards"]
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

    const edit_button = document.getElementById("edit-button");
    edit_button.addEventListener("htmx:beforeOnLoad", (event) => {
        if (event.detail.elt === edit_button) {
            editing = true;
            const modal = document.getElementById("modal");
            modal.style.visibility = "visible";
            modal.style.opacity = "1.0";
        }
    });

    function transition(time) {
        card_front.style.transition = "all " + time + "s ease-in-out";
        card_back.style.transition = "all " + time + "s ease-in-out";
        card_next.style.transition = "all " + time + "s ease-in-out";
    }

    function getTransform(el) {
        let transform = el.style.transform;
        if (transform.length === 0) return {
            rotate: "0.0rad",
            scale: "1.0"
        }
        const parts = transform.split("(");
        const rotate = parts[1].split(")")[0];
        const scale = parts[2].split(")")[0];
        return {
            rotate: rotate,
            scale: scale
        }
    }

    function setTransform(el, rotate, scale) {
        el.style.transform = "rotateY(" + rotate + ") scale(" + scale + ")";
    }

    function rotate() {
        const angle = spin_fraction * Math.PI;

        const front_t = getTransform(card_front);
        setTransform(card_front, angle + "rad", front_t.scale);

        const back_t = getTransform(card_back);
        setTransform(card_back, (angle + Math.PI) + "rad", back_t.scale);
    }

    function move(x, y) {
        card_front.style.left = x + "px";
        card_front.style.top = y + "px";
        card_back.style.left = x + "px";
        card_back.style.top = y + "px";
    }

    function resize(fraction) {
        const front_t = getTransform(card_front);
        setTransform(card_front, front_t.rotate, fraction);

        const back_t = getTransform(card_back);
        setTransform(card_back, back_t.rotate, fraction);
    }

    function resetSize() {
        resize(1.0);
    }

    function resizeNext(fraction) {
        card_next.style.transform = "scale(" + fraction + ")";
    }

    function finishPack() {
        if (post_results) {
            const pack_id = document.getElementById("game-container").getAttribute("data-pack-id");
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

    function updateEditButton(edit_card_id, editable) {
        const edit_button = document.getElementById("edit-button");
        edit_button.disabled = !editable;
        if (!editable) edit_card_id = -1;
        edit_button.setAttribute("hx-get", `/forms/cards/${edit_card_id}/edit`);
        htmx.process(edit_button);
    }

    function switchToNextCard(first_card=false) {
        if (!first_card) cards.shift();
        if (cards.length === 0) {
            card_front.remove();
            card_back.remove();
            finishPack();
            updateEditButton(-1, false);
            return;
        }
        transition(0);
        card_front.innerHTML = cards[0]["front"];
        card_back.innerHTML = cards[0]["back"];
        card_front.style.background = cards[0]["front-color"];
        card_back.style.background = cards[0]["back-color"];
        updateEditButton(cards[0]["id"], cards[0]["is-owner"]);
        if (cards.length > 1) {
            card_next.innerHTML = cards[1]["front"];
            card_next.style.background = cards[1]["front-color"];
        } else {
            card_next.remove();
        }
    }

    function moveToStack(stack, correct) {
        if (cards.length === 0 || editing)
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
        let card_font_size = getComputedStyle(card_container).fontSize;
        let font_size = (stack_rect.height / card_rect.height) * Number(card_font_size.replace("px", ""));

        console.log(font_size);

        // set card transition in motion
        transition(1);

        const stack_mx = stack_rect.left + stack_rect.right;
        const stack_my = stack_rect.top + stack_rect.bottom;
        const card_mx = card_rect.left + card_rect.right;
        const card_my = card_rect.top + card_rect.bottom;

        const target_x = (stack_mx - card_mx) * 0.5;
        const target_y = (stack_my - card_my) * 0.5;

        move(target_x, target_y);

        const resize_fraction = stack_rect.width / card_rect.width;
        resize(resize_fraction);
        resizeNext(1.0);

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

            // set stack styles
            stack.replaceChildren(...card_back.cloneNode(true).childNodes);
            stack.style.fontSize = font_size + "px";
            stack.style.background = card_back.style.background;

            resizeNext(0.8);

            switchToNextCard();
            }, 1000);
    }

    stack_correct.addEventListener("click", function(event) {
        if (switched && !editing) {
            moveToStack(stack_correct, true);
        }
    });

    stack_incorrect.addEventListener("click", function(event) {
        if (switched && !editing) {
            moveToStack(stack_incorrect, false);
        }
    });

    card_container.addEventListener("mousedown", function(event) {
        if (transitioning || editing) {
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

    function flipCard() {
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

    document.addEventListener("keydown", function (event) {
        if (event.key === " " && !editing) {
            event.preventDefault();
        }

        if (transitioning || editing) {
            return;
        }

        switch (event.key) {
            case " ":
                // mark as not clicked.
                clicked = false;
                side = "neither";
                flipped = false;
                switched = !switched;

                // make transitions be 1 seconds
                transition(1);

                flipCard();
                break;
            case "X":
            case "x":
                if (switched) {
                    moveToStack(stack_incorrect, false);
                }
                break;
            case "C":
            case "c":
                if (switched) {
                    moveToStack(stack_correct, true);
                }
                break;
        }
    });

    document.onmouseup = function(event) {
        if (side === "neither" || editing) {
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

        flipCard();
    }

    document.onmousemove = function(event) {
        // if mouse not down, then ignore
        if (!clicked || editing) {
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
    const edit_close = document.getElementById("edit-close");
    edit_close.addEventListener("click", (event) => {
        editing = false;
        const modal = document.getElementById("modal");
        modal.style.visibility = "hidden";
        modal.style.opacity = "0.0";

        fetch(
            `/api/cards/${cards[0]["id"]}/`, {
                method: "POST"
            }
        ).then(response => {
            if (response.ok) {
                return response.json();
            }
            return Promise.reject(response);
        }).then(
            json => {
                cards[0] = json;
                const card_front = document.getElementById("card-front");
                const card_back = document.getElementById("card-back");
                card_front.innerHTML = cards[0]["front"];
                card_back.innerHTML = cards[0]["back"];
                card_front.style.background = cards[0]["front-color"];
                card_back.style.background = cards[0]["back-color"];
            }
        ).catch(error => {
            console.log(error);
            error.text().then(
                text => {
                    const content = document.getElementById("content");
                    content.innerHTML = text;
                }
            );
        });
    });
}

document.body.addEventListener("htmx:load", function(event) {
    if (editing) {
        onLoadEditor();
    } else {
        const game_container = document.getElementById("game-container");
        const pack_id = game_container.getAttribute("data-pack-id");
        const quiz_style = game_container.getAttribute("data-quiz-style");
        const num_cards = Number(game_container.getAttribute("data-num-cards"));
        let body = {
            "quiz-style": quiz_style,
            "num-cards": num_cards
        }
        const spotlight_id = game_container.getAttribute("data-spotlight-id");
        if (spotlight_id !== "") {
            body["spotlight-id"] = Number(spotlight_id);
        }
        fetch(
            `/api/packs/${pack_id}/cards/`, {
                method: "POST",
                "headers": {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
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