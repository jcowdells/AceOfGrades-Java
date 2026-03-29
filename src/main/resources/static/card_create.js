let edit_angle = 0;
let edit_flipped = false;

document.body.addEventListener("htmx:load", function(event) {
    // on load, reset angle and flip
    edit_angle = 0;
    edit_flipped = false;

    // card elements
    const card_front = document.getElementById("edit-front");
    const card_back = document.getElementById("edit-back");

    // input elements
    const flip_button = document.getElementById("flip-button");
    const front_color = document.getElementById("front_color");
    const back_color = document.getElementById("back_color");

    // update styles
    card_front.children[0].style.background = front_color.value;
    card_back.children[0].style.background = back_color.value;

    card_front.style.transition = "all 1s ease-in-out";
    card_back.style.transition = "all 1s ease-in-out";

    // on click the flip button, flip the cards
    flip_button.onclick = function() {
        // flip cards by 180 degrees.
        edit_flipped = !edit_flipped;
        if (edit_flipped) {
            edit_angle = 180;
            flip_button.value = "Unflip";
        } else {
            edit_angle = 0;
            flip_button.value = "Flip";
        }

        card_front.style.transform = "rotateY(" + edit_angle + "deg)";
        card_back.style.transform = "rotateY(" + (edit_angle + 180) + "deg)";
    }

    // on update colour, change the card colour visually
    front_color.onchange = function() {
        card_front.children[0].style.background = front_color.value;
    }

    back_color.onchange = function() {
        card_back.children[0].style.background = back_color.value;
    }
});