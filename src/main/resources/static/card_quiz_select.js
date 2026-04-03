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

const delete_buttons = document.getElementsByClassName("delete-button")
for (let i = 0; i < delete_buttons.length; i++) {
    delete_buttons[i].addEventListener("click", function () {
        const spotlight_id = delete_buttons[i].dataset["spotlightId"];
        fetch(
            `/api/spotlights/${spotlight_id}/delete`, {
                method: "POST"
            }
        ).then(
            response => {
                if (response.ok) {
                    return response.json();
                }
                return Promise.reject(response);
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
        delete_buttons[i].parentElement.parentElement.remove();
    })
}