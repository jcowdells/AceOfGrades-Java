const packs_container = document.getElementById("packs-container");
const search_input = document.getElementById("search-input");

async function search(input) {
    let cards_data = [];
    input = input.toLowerCase();
    packs_container.querySelectorAll(".card").forEach((card) => {
        if (card.textContent.toLowerCase().indexOf(input) > -1) {
            cards_data.push({
                "card": card,
                "visibility": ""
            });
        } else {
            cards_data.push({
                "card": card,
                "visibility": "none"
            });
        }
    });
    return cards_data;
}

search_input.addEventListener("keyup", function (event) {
    console.log("Change");
    let input = search_input.value;
    search(input).then(cards_data => cards_data.forEach(card => card.card.style.display = card.visibility));
    console.log("Done");
});