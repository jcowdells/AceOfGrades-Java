// card elements
let card_front = document.getElementById("card-front")
let card_back = document.getElementById("card-back")

// input elements
let flip_button = document.getElementById("flip-button")
let front_color = document.getElementById("front_color")
let back_color = document.getElementById("back_color")

card_front.children[0].style.background = front_color.value
card_back.children[0].style.background = back_color.value

var angle = 0;
var flipped = false;

card_front.style.transition = "all 1s ease-in-out"
card_back.style.transition = "all 1s ease-in-out"

flip_button.onclick = function() {
    // flip cards by 180 degrees.
    flipped = !flipped;
    if (flipped) {
        angle = 180
        flip_button.value = "Unflip"
    } else {
        angle = 0
        flip_button.value = "Flip"
    }

    card_front.style.transform = "rotateY(" + angle + "deg)"
    card_back.style.transform = "rotateY(" + (angle + 180) + "deg)"
}

front_color.onchange = function() {
    card_front.children[0].style.background = front_color.value
}

back_color.onchange = function() {
    card_back.children[0].style.background = back_color.value
}