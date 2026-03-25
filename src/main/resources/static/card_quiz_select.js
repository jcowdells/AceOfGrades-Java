// get description elements
const descriptions = {
    "random": document.getElementById("random-description"),
    "weakest": document.getElementById("weakest-description"),
    "burnout": document.getElementById("burnout-description")
}
const selected_description = document.getElementById("selected-description");

// form elements
const form_select = document.getElementById("quiz-style");

function changeDescription() {
    selected_description.innerHTML = descriptions[form_select.value].innerHTML;
}

form_select.addEventListener("change", changeDescription);

// on page load, do the right one
changeDescription();